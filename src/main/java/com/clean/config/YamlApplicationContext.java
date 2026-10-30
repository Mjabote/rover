package com.clean.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YamlApplicationContext implements ApplicationContext{
    private static final Logger log = LoggerFactory.getLogger(YamlApplicationContext.class);
    private static final Set<String> ROOT_KEYS = Set.of("properties", "beans");
    private static final Set<String> BEAN_KEYS = Set.of("class", "factory-bean", "factory-method", "args", "calls");
    private static final Set<String> CALL_KEYS = Set.of("method", "args");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?}");

    private final Map<String, String> properties = new LinkedHashMap<>();
    private final Map<String, Map<String, Object>> definitions = new LinkedHashMap<>();
    private final Map<String, Object> singletons = new LinkedHashMap<>();
    private final Set<String> inCreation = new LinkedHashSet<>();

    private YamlApplicationContext(Map<String, Object> root, String source) {
        checkKeys(root, ROOT_KEYS, source);
        if (root.get("properties") != null) {
            flatten("", asMap(root.get("properties"), source + " 'properties'"), properties);
        }
        Map<String, Object> beans = asMap(root.get("beans"), source + " 'beans'");
        for (Map.Entry<String, Object> bean : beans.entrySet()) {
            definitions.put(bean.getKey(), asMap(bean.getValue(), "bean '" + bean.getKey() + "'"));
        }
        for (String name : definitions.keySet()) {                  // eager singletons, like Spring
            bean(name);
        }
        log.info("Created {} singleton bean(s) from {}: {}", singletons.size(), source, singletons.keySet());
    }

    public static YamlApplicationContext fromClasspath(String resource) {
        String path = resource.startsWith("/") ? resource : "/" + resource;
        try (InputStream in = YamlApplicationContext.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new BeansException("Configuration " + resource + " not found on the classpath");
            }
            return fromReader(new InputStreamReader(in, StandardCharsets.UTF_8), "classpath:" + resource);
        } catch (IOException e) {
            throw new BeansException("Cannot read configuration " + resource + ": " + e.getMessage(), e);
        }
    }
    public static YamlApplicationContext fromYaml(String yaml) {
        return fromReader(new StringReader(yaml), "inline YAML");
    }

    private static YamlApplicationContext fromReader(Reader reader, String source) {
        Object root;
        try {
            root = new Yaml(new SafeConstructor(new LoaderOptions())).load(reader);   // plain maps/lists only
        } catch (YAMLException e) {
            throw new BeansException("Invalid YAML in " + source + ": " + e.getMessage(), e);
        }
        return new YamlApplicationContext(asMap(root, source), source);
    }

    @Override
    public <T> T getBean(String name, Class<T> type) {
        Object bean = singletons.get(name);
        if (bean == null) {
            throw new BeansException("No bean named '" + name + "' (known: " + singletons.keySet() + ")");
        }
        if (!type.isInstance(bean)) {
            throw new BeansException("Bean '" + name + "' is a " + bean.getClass().getName()
                    + ", not a " + type.getName());
        }
        return type.cast(bean);
    }

    @Override
    public <T> T getBean(Class<T> type) {
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, Object> bean : singletons.entrySet()) {
            if (type.isInstance(bean.getValue())) {
                matches.add(bean.getKey());
            }
        }
        if (matches.isEmpty()) {
            throw new BeansException("No bean of type " + type.getName());
        }
        if (matches.size() > 1) {
            throw new BeansException("Several beans of type " + type.getName() + ": " + matches
                    + " (use getBean(name, type))");
        }
        return type.cast(singletons.get(matches.get(0)));
    }

    @Override
    public boolean containsBean(String name) {
        return singletons.containsKey(name);
    }

    @Override
    public Set<String> beanNames() {
        return Collections.unmodifiableSet(singletons.keySet());
    }

    @Override
    public String getProperty(String key) {
        String value = System.getProperty(key);                                   // -Dkey=value
        if (value == null) {
            value = System.getenv(key.toUpperCase().replace('.', '_').replace('-', '_'));   // KEY_NAME
        }
        return value != null ? value : properties.get(key);
    }

    @Override
    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new BeansException("Missing property '" + key + "'");
        }
        return value;
    }


    private Object bean(String name) {
        Object existing = singletons.get(name);
        if (existing != null) {
            return existing;
        }
        Map<String, Object> definition = definitions.get(name);
        if (definition == null) {
            throw new BeansException("No bean named '" + name + "' (known: " + definitions.keySet() + ")");
        }
        if (!inCreation.add(name)) {
            throw new BeansException("Circular reference: " + String.join(" -> ", inCreation) + " -> " + name);
        }
        try {
            Object instance = create(name, definition);
            singletons.put(name, instance);
            log.debug("Created bean '{}' ({})", name, instance.getClass().getName());
            return instance;
        } finally {
            inCreation.remove(name);
        }
    }

    private Object create(String name, Map<String, Object> definition) {
        checkKeys(definition, BEAN_KEYS, "bean '" + name + "'");
        String className = asString(definition.get("class"), "bean '" + name + "' class");
        String factoryBean = asString(definition.get("factory-bean"), "bean '" + name + "' factory-bean");
        String factoryMethod = asString(definition.get("factory-method"), "bean '" + name + "' factory-method");
        List<Object> args = resolveArgs(name, definition.get("args"));

        Object instance;
        if (factoryBean != null) {
            if (factoryMethod == null) {
                throw new BeansException("Bean '" + name + "': factory-bean needs a factory-method");
            }
            instance = BeanReflection.invoke(bean(factoryBean), factoryMethod, args, name);
        } else if (className != null) {
            Class<?> type = loadClass(className, name);
            instance = factoryMethod == null
                    ? BeanReflection.construct(type, args, name)
                    : BeanReflection.invokeStatic(type, factoryMethod, args, name);
        } else {
            throw new BeansException("Bean '" + name + "': 'class' or 'factory-bean' is required");
        }
        if (instance == null) {
            throw new BeansException("Bean '" + name + "': factory method " + factoryMethod + " returned null");
        }
        if (className != null && factoryBean != null && !loadClass(className, name).isInstance(instance)) {
            throw new BeansException("Bean '" + name + "' is a " + instance.getClass().getName()
                    + ", not the declared " + className);
        }

        for (Object call : asList(definition.get("calls"), "bean '" + name + "' calls")) {
            Map<String, Object> callMap = asMap(call, "bean '" + name + "' call");
            checkKeys(callMap, CALL_KEYS, "bean '" + name + "' call");
            String method = asString(callMap.get("method"), "bean '" + name + "' call method");
            if (method == null) {
                throw new BeansException("Bean '" + name + "': every call needs a 'method'");
            }
            Object result = BeanReflection.invoke(instance, method, resolveArgs(name, callMap.get("args")), name);
            if (result != null && instance.getClass().isInstance(result)) {
                instance = result;                     // builder / immutable "with" style
            }
        }
        return instance;
    }

    private List<Object> resolveArgs(String beanName, Object rawArgs) {
        List<Object> resolved = new ArrayList<>();
        List<Object> args = asList(rawArgs, "bean '" + beanName + "' args");
        for (int i = 0; i < args.size(); i++) {
            resolved.add(resolveValue(beanName + ".args[" + i + "]", args.get(i)));
        }
        return resolved;
    }

    private Object resolveValue(String where, Object value) {
        if (value instanceof String text) {
            return resolvePlaceholders(text, where);
        }
        if (value instanceof List<?> list) {
            List<Object> resolved = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                resolved.add(resolveValue(where + "[" + i + "]", list.get(i)));
            }
            return resolved;
        }
        if (value instanceof Map<?, ?>) {
            Map<String, Object> map = asMap(value, where);
            if (map.containsKey("ref")) {
                if (map.size() != 1) {
                    throw new BeansException(where + ": { ref: ... } cannot have other keys " + map.keySet());
                }
                return bean(asString(map.get("ref"), where + " ref"));
            }
            return create(where, map);                 // inline bean, not a shared singleton
        }
        return value;                                  // number, boolean, null
    }

    private String resolvePlaceholders(String text, String where) {
        Matcher m = PLACEHOLDER.matcher(text);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String value = getProperty(m.group(1).strip());
            if (value == null) {
                value = m.group(2);                    // ${key:default}
            }
            if (value == null) {
                throw new BeansException(where + ": unresolved placeholder ${" + m.group(1) + "}");
            }
            m.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        m.appendTail(out);
        return out.toString();
    }


    private static Class<?> loadClass(String className, String beanName) {
        try {
            return Class.forName(className, true, YamlApplicationContext.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new BeansException("Bean '" + beanName + "': class not found: " + className, e);
        }
    }

    private static void flatten(String prefix, Map<String, Object> map, Map<String, String> into) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix + entry.getKey();
            if (entry.getValue() instanceof Map<?, ?> nested) {
                flatten(key + ".", asMap(nested, key), into);
            } else if (entry.getValue() != null) {
                into.put(key, entry.getValue().toString());
            }
        }
    }

    private static void checkKeys(Map<String, Object> map, Set<String> allowed, String where) {
        Set<String> unknown = new TreeSet<>(map.keySet());
        unknown.removeAll(allowed);
        if (!unknown.isEmpty()) {
            throw new BeansException(where + ": unknown key(s) " + unknown + " (allowed: " + new TreeSet<>(allowed) + ")");
        }
    }

    private static Map<String, Object> asMap(Object value, String where) {
        if (!(value instanceof Map<?, ?> raw)) {
            throw new BeansException(where + " must be a YAML mapping (key: value)");
        }
        Map<String, Object> map = new LinkedHashMap<>();
        raw.forEach((k, v) -> map.put(String.valueOf(k), v));
        return map;
    }

    private static List<Object> asList(Object value, String where) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new BeansException(where + " must be a YAML list ([a, b] or - items)");
        }
        return new ArrayList<>(list);
    }

    private static String asString(Object value, String where) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map || value instanceof List) {
            throw new BeansException(where + " must be a single value");
        }
        return value.toString();
    }

}

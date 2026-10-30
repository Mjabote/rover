package com.clean.config;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class BeanReflection {

    private static final Object NO_MATCH = new Object();

    private static final Map<Class<?>, Class<?>> BOXES = Map.of(
            int.class, Integer.class, long.class, Long.class, double.class, Double.class,
            float.class, Float.class, short.class, Short.class, byte.class, Byte.class,
            char.class, Character.class, boolean.class, Boolean.class);

    private BeanReflection() {
    }

    static Object construct(Class<?> type, List<Object> args, String beanName) {
        if (Modifier.isAbstract(type.getModifiers())) {
            throw new BeansException("Bean '" + beanName + "': " + type.getName()
                    + " is abstract or an interface, it cannot be instantiated");
        }
        return call(Arrays.asList(type.getConstructors()), null, args, beanName,
                "constructor of " + type.getName());
    }

    static Object invokeStatic(Class<?> type, String methodName, List<Object> args, String beanName) {
        List<Method> candidates = Arrays.stream(type.getMethods())
                .filter(m -> m.getName().equals(methodName) && Modifier.isStatic(m.getModifiers()))
                .toList();
        return call(candidates, null, args, beanName, "static method " + type.getName() + "." + methodName);
    }

    static Object invoke(Object target, String methodName, List<Object> args, String beanName) {
        List<Method> candidates = Arrays.stream(target.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName) && !Modifier.isStatic(m.getModifiers()))
                .toList();
        return call(candidates, target, args, beanName, "method " + target.getClass().getName() + "." + methodName);
    }

    private static Object call(List<? extends Executable> candidates, Object target, List<Object> args,
                               String beanName, String what) {
        Executable best = null;
        Object[] bestArgs = null;
        int bestScore = -1;
        boolean ambiguous = false;

        for (Executable candidate : candidates) {
            if (candidate.getParameterCount() != args.size() || candidate.isVarArgs()) {
                continue;
            }
            Object[] converted = convertAll(candidate.getParameterTypes(), args);
            if (converted == null) {
                continue;
            }
            int score = exactMatches(candidate.getParameterTypes(), args);
            if (score > bestScore) {
                best = candidate;
                bestArgs = converted;
                bestScore = score;
                ambiguous = false;
            } else if (score == bestScore) {
                ambiguous = true;
            }
        }

        if (best == null) {
            throw new BeansException("Bean '" + beanName + "': no public " + what + " accepts " + describe(args)
                    + (candidates.isEmpty() ? " (none exists)" : " (candidates: " + signatures(candidates) + ")"));
        }
        if (ambiguous) {
            throw new BeansException("Bean '" + beanName + "': several " + what + " overloads accept "
                    + describe(args) + " (" + signatures(candidates) + ")");
        }
        try {
            return best instanceof Constructor<?> constructor
                    ? constructor.newInstance(bestArgs)
                    : ((Method) best).invoke(target, bestArgs);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new BeansException("Bean '" + beanName + "': " + what + " failed: " + cause.getMessage(), cause);
        } catch (ReflectiveOperationException e) {
            throw new BeansException("Bean '" + beanName + "': cannot call " + what + ": " + e.getMessage(), e);
        }
    }
    private static int exactMatches(Class<?>[] types, List<Object> args) {
        int exact = 0;
        for (int i = 0; i < types.length; i++) {
            if (args.get(i) != null && box(types[i]).isInstance(args.get(i))) {
                exact++;
            }
        }
        return exact;
    }
    private static Object[] convertAll(Class<?>[] types, List<Object> args) {
        Object[] converted = new Object[args.size()];
        for (int i = 0; i < converted.length; i++) {
            Object value = convert(args.get(i), types[i]);
            if (value == NO_MATCH) {
                return null;
            }
            converted[i] = value;
        }
        return converted;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Object convert(Object value, Class<?> type) {
        Class<?> boxed = box(type);
        if (value == null) {
            return type.isPrimitive() ? NO_MATCH : null;
        }
        if (boxed.isInstance(value)) {
            return value;
        }
        if (!(value instanceof String || value instanceof Number || value instanceof Boolean)) {
            return NO_MATCH;                                  // a bean of the wrong type
        }
        String text = value.toString().strip();
        try {
            if (boxed == String.class)    return value.toString();
            if (boxed == Character.class) return text.length() == 1 ? text.charAt(0) : NO_MATCH;
            if (boxed == Integer.class)   return Integer.valueOf(text);
            if (boxed == Long.class)      return Long.valueOf(text);
            if (boxed == Double.class)    return Double.valueOf(text);
            if (boxed == Float.class)     return Float.valueOf(text);
            if (boxed == Short.class)     return Short.valueOf(text);
            if (boxed == Byte.class)      return Byte.valueOf(text);
            if (boxed == Boolean.class) {
                return "true".equalsIgnoreCase(text) ? Boolean.TRUE
                        : "false".equalsIgnoreCase(text) ? Boolean.FALSE : NO_MATCH;
            }
            if (type.isEnum())            return Enum.valueOf((Class) type, text);
        } catch (IllegalArgumentException e) {               // includes NumberFormatException
            return NO_MATCH;
        }
        return NO_MATCH;
    }

    private static Class<?> box(Class<?> type) {
        return type.isPrimitive() ? BOXES.get(type) : type;
    }
    private static String describe(List<Object> args) {
        List<String> parts = new ArrayList<>();
        for (Object arg : args) {
            parts.add(arg == null ? "null" : arg.getClass().getSimpleName() + " " + arg);
        }
        return parts.toString();
    }

    private static String signatures(List<? extends Executable> executables) {
        List<String> parts = new ArrayList<>();
        for (Executable e : executables) {
            parts.add(Arrays.stream(e.getParameterTypes()).map(Class::getSimpleName).toList().toString()
                    .replace('[', '(').replace(']', ')'));
        }
        return String.join(", ", parts);
    }


}
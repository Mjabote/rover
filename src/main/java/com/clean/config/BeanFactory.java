package com.clean.config;

import java.util.Set;

public interface BeanFactory {

    <T> T getBean(String name, Class<T> type);

    <T> T getBean(Class<T> type);

    boolean containsBean(String name);

    Set<String> beanNames();
}

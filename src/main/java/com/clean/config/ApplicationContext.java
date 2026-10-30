package com.clean.config;

import java.util.Set;

/** A container of singleton objects ("beans"), created once at startup, like Spring's ApplicationContext. */
public interface ApplicationContext {

    <T> T getBean(String name, Class<T> type);

    <T> T getBean(Class<T> type);

    boolean containsBean(String name);

    Set<String> beanNames();

    String getProperty(String key);

    String getRequiredProperty(String key);
}
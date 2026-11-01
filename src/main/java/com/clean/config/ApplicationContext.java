package com.clean.config;

/** A container of singleton objects ("beans"), created once at startup, like Spring's ApplicationContext. */
public interface ApplicationContext extends BeanFactory, PropertyResolver {
}

package com.clean.config;

public interface PropertyResolver {

    String getProperty(String key);

    String getRequiredProperty(String key);
}

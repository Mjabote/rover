package com.clean.domain.factory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class RoverCatalog {
    private final Map<RoverFactoryType, RoverFactory<?>> factories = new EnumMap<>(RoverFactoryType.class);

    public RoverCatalog register(RoverFactoryType type, RoverFactory<?> factory) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        if (factories.putIfAbsent(type, factory) != null) {
            throw new IllegalArgumentException("Rover type " + type + " is already registered");
        }
        return this;
    }

    public RoverFactory<?> factoryFor(RoverFactoryType type) {
        RoverFactory<?> factory = factories.get(Objects.requireNonNull(type, "type must not be null"));
        if (factory == null) {
            throw new IllegalStateException("No factory registered for rover type " + type);
        }
        return factory;
    }

}

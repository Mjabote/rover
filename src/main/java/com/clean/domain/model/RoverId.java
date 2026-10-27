package com.clean.domain.model;

import java.util.Objects;
import java.util.UUID;

public record RoverId(UUID value) {

    public RoverId {
        Objects.requireNonNull(value, "RoverId value must not be null");
    }

    public static RoverId newId() {
        return new RoverId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

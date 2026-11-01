package com.clean.domain.model.ground;

import java.util.Objects;
import java.util.UUID;

public record GroundId(UUID value) {

    public GroundId{
        Objects.requireNonNull(value, "GroundId value must not be null");
    }

    public static GroundId newId() {
        return new GroundId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

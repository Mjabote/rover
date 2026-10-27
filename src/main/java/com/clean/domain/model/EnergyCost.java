package com.clean.domain.model;

import java.util.Objects;

public record EnergyCost(Fuel fuel, Charge battery) {

    public static final EnergyCost NONE = new EnergyCost(Fuel.ZERO, Charge.ZERO);

    public EnergyCost {
        Objects.requireNonNull(fuel, "fuel must not be null");
        Objects.requireNonNull(battery, "battery must not be null");
    }

    public static EnergyCost of(long fuelMillilitres, long batteryMilliampHours) {
        return new EnergyCost(Fuel.ofMillilitres(fuelMillilitres), Charge.ofMilliampHours(batteryMilliampHours));
    }


}

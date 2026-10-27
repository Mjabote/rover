package com.clean.domain.model;

import java.util.Objects;

public record EnergyLevel(Fuel fuel, Charge battery) {

    public EnergyLevel {
        Objects.requireNonNull(fuel, "fuel must not be null");
        Objects.requireNonNull(battery, "battery must not be null");
    }

    public static EnergyLevel of(long fuelMillilitres, long batteryMilliampHours) {
        return new EnergyLevel(Fuel.ofMillilitres(fuelMillilitres), Charge.ofMilliampHours(batteryMilliampHours));
    }

    public EnergyLevel minus(EnergyCost cost) {
        if (!canPay(cost)) {
            throw new IllegalStateException("Cannot pay " + cost + " from " + this);
        }
        return new EnergyLevel(fuel.minus(cost.fuel()), battery.minus(cost.battery()));
    }
    public boolean hasFuelFor(EnergyCost cost) {
        return fuel.isAtLeast(cost.fuel());
    }

    public boolean hasBatteryFor(EnergyCost cost) {
        return battery.isAtLeast(cost.battery());
    }

    public boolean canPay(EnergyCost cost) {
        return hasFuelFor(cost) && hasBatteryFor(cost);
    }

    public String toString() {
        return fuel + " + " + battery;
    }

}

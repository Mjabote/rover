package com.clean.domain.model.energy;

import java.util.Objects;

public record EnergyCapacity(Fuel maxFuel, Charge maxBattery) {

    public EnergyCapacity {
        Objects.requireNonNull(maxFuel, "maxFuel must not be null");
        Objects.requireNonNull(maxBattery, "maxBattery must not be null");
    }

    public static EnergyCapacity of(long fuelMillilitres, long batteryMilliampHours) {
        return new EnergyCapacity(Fuel.ofMillilitres(fuelMillilitres), Charge.ofMilliampHours(batteryMilliampHours));
    }

    public EnergyLevel full() {
        return new EnergyLevel(maxFuel, maxBattery);
    }

    public boolean canHold(EnergyLevel level) {
        return maxFuel.isAtLeast(level.fuel()) && maxBattery.isAtLeast(level.battery());
    }
    @Override
    public String toString() {
        return maxFuel + " tank, " + maxBattery + " battery";
    }
}

package com.clean.domain.model.rover;

import com.clean.domain.model.energy.Charge;
import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.energy.Fuel;

public interface PoweredRover {

    EnergyLevel energy();

    EnergyCapacity capacity();

    default Fuel fuel() {
        return energy().fuel();
    }

    default Charge battery() {
        return energy().battery();
    }

    default boolean hasEnoughFuelFor(EnergyCost cost) {
        return energy().hasFuelFor(cost);
    }

    default boolean hasEnoughBatteryFor(EnergyCost cost) {
        return energy().hasBatteryFor(cost);
    }

    default boolean canAfford(EnergyCost cost) {
        return energy().canPay(cost);
    }
}

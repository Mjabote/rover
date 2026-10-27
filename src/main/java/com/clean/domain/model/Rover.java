package com.clean.domain.model;

public interface Rover {

    RoverId id();

    GroundId groundId();

    Direction direction();

    EnergyLevel energy();

    EnergyCapacity capacity();

    CellPosition position();

    CellPosition nextPosition();

    EnergyCost costOfNextMove();

    void turnLeft();

    void turnRight();

    EnergyCost advance();


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

package com.clean.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnergyTest {

    @Test
    void fuelAndChargeRejectNegativeAmounts() {
        assertThrows(IllegalArgumentException.class, () -> Fuel.ofMillilitres(-1));
        assertThrows(IllegalArgumentException.class, () -> Charge.ofMilliampHours(-1));
        assertThrows(IllegalArgumentException.class, () -> Fuel.ofMillilitres(5).minus(Fuel.ofMillilitres(6)));
    }

    @Test
    void energyLevelMinusDeductsCostAndRefusesOverdraft() {
        EnergyLevel level = EnergyLevel.of(100, 50);

        assertEquals(EnergyLevel.of(70, 40), level.minus(EnergyCost.of(30, 10)));
        assertFalse(level.canPay(EnergyCost.of(10, 51)));
        assertThrows(IllegalStateException.class, () -> level.minus(EnergyCost.of(101, 0)));
    }

    @Test
    void capacityCanHoldOnlyLevelsWithinBothLimits() {
        EnergyCapacity capacity = EnergyCapacity.of(100, 50);

        assertTrue(capacity.canHold(capacity.full()));
        assertFalse(capacity.canHold(EnergyLevel.of(101, 0)));
        assertFalse(capacity.canHold(EnergyLevel.of(0, 51)));
    }
}

package com.clean.domain.model.energy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnergyValueObjectsTest {

    @Test
    void fuelArithmeticAndComparison() {
        Fuel ten = Fuel.ofMillilitres(10);
        Fuel four = Fuel.ofMillilitres(4);

        assertEquals(Fuel.ofMillilitres(14), ten.plus(four));
        assertEquals(Fuel.ofMillilitres(6), ten.minus(four));
        assertTrue(ten.isAtLeast(four));
        assertTrue(Fuel.ZERO.isZero());
        assertEquals(40.0, four.percentOf(ten));
        assertEquals(0.0, four.percentOf(Fuel.ZERO));
        assertTrue(four.compareTo(ten) < 0);
    }

    @Test
    void chargeArithmeticAndComparison() {
        Charge ten = Charge.ofMilliampHours(10);
        Charge four = Charge.ofMilliampHours(4);

        assertEquals(Charge.ofMilliampHours(14), ten.plus(four));
        assertEquals(Charge.ofMilliampHours(6), ten.minus(four));
        assertFalse(four.isAtLeast(ten));
        assertFalse(ten.isZero());
        assertEquals(40.0, four.percentOf(ten));
        assertEquals(0.0, four.percentOf(Charge.ZERO));
        assertTrue(ten.compareTo(four) > 0);
        assertThrows(IllegalArgumentException.class, () -> four.minus(ten));
    }

    @Test
    void energyRecordsRejectNullParts() {
        assertThrows(NullPointerException.class, () -> new EnergyLevel(null, Charge.ZERO));
        assertThrows(NullPointerException.class, () -> new EnergyCost(Fuel.ZERO, null));
        assertThrows(NullPointerException.class, () -> new EnergyCapacity(null, Charge.ZERO));
    }

    @Test
    void energyLevelChecksFuelAndBatterySeparately() {
        EnergyLevel level = EnergyLevel.of(10, 0);
        EnergyCost cost = EnergyCost.of(5, 1);

        assertTrue(level.hasFuelFor(cost));
        assertFalse(level.hasBatteryFor(cost));
        assertFalse(level.canPay(cost));
        assertTrue(level.canPay(EnergyCost.NONE));
    }
}

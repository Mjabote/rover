package com.clean.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

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
    void cellSizeMustBePositiveAndFinite() {
        assertEquals(2.5, CellSize.ofMetre(2.5).metres());
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(0));
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(-1));
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(Double.POSITIVE_INFINITY));
    }

    @Test
    void idsAreUniqueAndRejectNull() {
        assertNotEquals(RoverId.newId(), RoverId.newId());
        assertNotEquals(GroundId.newId(), GroundId.newId());
        assertThrows(NullPointerException.class, () -> new RoverId(null));
        assertThrows(NullPointerException.class, () -> new GroundId(null));

        GroundId id = GroundId.newId();
        assertEquals(id.value().toString(), id.toString());
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

    @Test
    void groundRejectsInvalidCellCollections() {
        CellSize size = CellSize.ofMetre(1);
        GroundId id = GroundId.newId();
        CellPosition origin = new CellPosition(0, 0);

        assertThrows(IllegalArgumentException.class, () -> new TestGround(id, size, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new TestGround(id, size, List.of(origin, origin)));
        assertEquals(1, new TestGround(id, size, List.of(origin)).totalCellCount());
    }

    private static final class TestGround extends AbstractGround {
        TestGround(GroundId id, CellSize cellSize, List<CellPosition> positions) {
            super(id, cellSize, positions);
        }
    }
}

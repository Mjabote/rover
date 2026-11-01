package com.clean.domain.model.rover;

import com.clean.domain.model.energy.Charge;
import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.energy.Fuel;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.geometry.Rotation;
import com.clean.domain.model.ground.GroundId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StandardRoverTest {

    @Test
    void advanceMovesOneCellInFacingDirection() {
        StandardRover rover = new StandardRover(RoverId.newId(), GroundId.newId(),
                new CellPosition(1, 2), Direction.N,
                EnergyCapacity.of(100, 100), EnergyLevel.of(100, 100));

        rover.turnLeft();
        rover.advance();

        assertEquals(Direction.W, rover.direction());
        assertEquals(new CellPosition(0, 2), rover.position());
    }

    @Test
    void advanceDeductsMoveCostFromEnergy() {
        StandardRover rover = new StandardRover(RoverId.newId(), GroundId.newId(),
                new CellPosition(0, 0), Direction.N,
                EnergyCapacity.of(100, 100), EnergyLevel.of(100, 100),
                (from, facing) -> from.neighbour(facing),
                (from, to) -> EnergyCost.of(10, 3),
                (current, rotation) -> rotation == Rotation.LEFT ? current.turnLeft() : current.turnRight());

        assertEquals(new CellPosition(0, 1), rover.nextPosition());
        assertEquals(EnergyCost.of(10, 3), rover.advance());
        assertEquals(Fuel.ofMillilitres(90), rover.fuel());
        assertEquals(Charge.ofMilliampHours(97), rover.battery());
        assertTrue(rover.canAfford(EnergyCost.of(90, 97)));
        assertEquals("StandardRover[0 1 N, fuel=Fuel[millilitres=90]/Fuel[millilitres=100], "
                + "battery=Charge[milliampHours=97]/Charge[milliampHours=100]]", rover.toString());
    }

    @Test
    void advanceFailsWhenBatteryIsInsufficient() {
        StandardRover rover = new StandardRover(RoverId.newId(), GroundId.newId(),
                new CellPosition(0, 0), Direction.N,
                EnergyCapacity.of(100, 100), EnergyLevel.of(100, 1),
                (from, facing) -> from.neighbour(facing),
                (from, to) -> EnergyCost.of(0, 2),
                (current, rotation) -> rotation == Rotation.LEFT ? current.turnLeft() : current.turnRight());

        assertThrows(IllegalStateException.class, rover::advance);
    }

    @Test
    void defaultStrategiesAreStandardOnes() {
        StandardRover rover = new StandardRover(RoverId.newId(), GroundId.newId(),
                new CellPosition(0, 0), Direction.E,
                EnergyCapacity.of(10, 10), EnergyLevel.of(10, 10));

        rover.turnRight();

        assertEquals(Direction.S, rover.direction());
        assertEquals(EnergyCost.NONE, rover.costOfNextMove());
        assertNotNull(rover.movementStrategy());
        assertNotNull(rover.energyStrategy());
        assertNotNull(rover.turnStrategy());
        assertEquals(EnergyCapacity.of(10, 10), rover.capacity());
    }

    @Test
    void constructorRejectsInitialEnergyAboveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new StandardRover(RoverId.newId(), GroundId.newId(),
                new CellPosition(0, 0), Direction.N,
                EnergyCapacity.of(10, 10), EnergyLevel.of(11, 10)));
    }

    @Test
    void advanceFailsWithoutMovingWhenFuelIsInsufficient() {
        StandardRover rover = new StandardRover(RoverId.newId(), GroundId.newId(),
                new CellPosition(0, 0), Direction.E,
                EnergyCapacity.of(100, 100), EnergyLevel.of(5, 100),
                (from, facing) -> from.neighbour(facing),
                (from, to) -> EnergyCost.of(10, 0),
                (current, rotation) -> rotation == Rotation.LEFT ? current.turnLeft() : current.turnRight());

        assertThrows(IllegalStateException.class, rover::advance);
        assertEquals(new CellPosition(0, 0), rover.position());
        assertEquals(EnergyLevel.of(5, 100), rover.energy());
    }
}

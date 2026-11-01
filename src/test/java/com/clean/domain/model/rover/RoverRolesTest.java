package com.clean.domain.model.rover;

import com.clean.domain.model.energy.Charge;
import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.energy.Fuel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoverRolesTest {

    @Test
    void roverIsTheSumOfItsRoles() {
        for (Class<?> role : List.of(RoverIdentity.class, RoverPose.class, Steerable.class,
                PoweredRover.class, Mobile.class)) {
            assertTrue(role.isAssignableFrom(Rover.class), "Rover should be a " + role.getSimpleName());
        }
    }

    @Test
    void roleInterfacesDoNotLeakUnrelatedMethods() {
        assertFalse(RoverPose.class.isAssignableFrom(PoweredRover.class));
        assertFalse(Steerable.class.isAssignableFrom(Mobile.class));
    }

    @Test
    void roverIdsAreUniqueAndRejectNull() {
        assertNotEquals(RoverId.newId(), RoverId.newId());
        assertThrows(NullPointerException.class, () -> new RoverId(null));
    }

    @Test
    void poweredRoverDefaultsDelegateToEnergyLevel() {
        PoweredRover powered = new PoweredRover() {
            @Override public EnergyLevel energy() { return EnergyLevel.of(5, 7); }
            @Override public EnergyCapacity capacity() { return EnergyCapacity.of(10, 10); }
        };

        assertEquals(Fuel.ofMillilitres(5), powered.fuel());
        assertEquals(Charge.ofMilliampHours(7), powered.battery());
        assertTrue(powered.hasEnoughFuelFor(EnergyCost.of(5, 0)));
        assertFalse(powered.hasEnoughBatteryFor(EnergyCost.of(0, 8)));
        assertFalse(powered.canAfford(EnergyCost.of(6, 1)));
    }
}

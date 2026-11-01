package com.clean.domain.factory;

import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.ground.GroundId;
import com.clean.domain.model.rover.RoverId;
import com.clean.domain.model.rover.StandardRover;
import com.clean.domain.strategy.EnergyStrategy;
import com.clean.domain.strategy.MovementStrategy;
import com.clean.domain.strategy.TurnStrategy;

public final class StandardRoverFactory implements RoverFactory<StandardRover> {
    private final EnergyCapacity capacity;
    private final EnergyLevel initialEnergy;

    public StandardRoverFactory(EnergyCapacity capacity, EnergyLevel initialEnergy) {
        this.capacity = capacity;
        this.initialEnergy = initialEnergy;
    }

    @Override
    public StandardRover create(GroundId groundId, CellPosition start, Direction direction) {
        return new StandardRover(RoverId.newId(), groundId, start, direction, capacity, initialEnergy);
    }
}
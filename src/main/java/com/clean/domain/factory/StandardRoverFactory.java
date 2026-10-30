package com.clean.domain.factory;

import com.clean.domain.model.*;
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
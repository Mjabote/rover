package com.clean.domain.model.rover;

import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.geometry.Rotation;
import com.clean.domain.model.ground.GroundId;
import com.clean.domain.strategy.*;

import java.util.Objects;

public class StandardRover extends AbstractRover{

    private final MovementStrategy movementStrategy;
    private final EnergyStrategy energyStrategy;
    private final TurnStrategy turnStrategy;

    public StandardRover(RoverId id,
                         GroundId groundId,
                         CellPosition position,
                         Direction direction,
                         EnergyCapacity capacity,
                         EnergyLevel initialEnergy) {
        this(id, groundId, position, direction, capacity, initialEnergy,
                new StandardMovement(), new StandardEnergy(), new StandardTurn());
    }

    public StandardRover(RoverId id,
                         GroundId groundId,
                         CellPosition position,
                         Direction direction,
                         EnergyCapacity capacity,
                         EnergyLevel initialEnergy,
                         MovementStrategy movementStrategy,
                         EnergyStrategy energyStrategy,
                         TurnStrategy turnStrategy) {
        super(id, groundId, position, direction, capacity, initialEnergy);
        this.movementStrategy = Objects.requireNonNull(movementStrategy, "movementStrategy must not be null");
        this.energyStrategy = Objects.requireNonNull(energyStrategy, "energyStrategy must not be null");
        this.turnStrategy = Objects.requireNonNull(turnStrategy, "turnStrategy must not be null");
    }

    @Override
    protected CellPosition destinationFrom(CellPosition from, Direction facing) {
        return movementStrategy.nextPosition(from, facing);
    }

    @Override
    protected EnergyCost moveCost(CellPosition from, CellPosition to) {

        return energyStrategy.costOfMove(from, to);
    }

    @Override
    protected Direction turned(Direction current, Rotation rotation) {

        return turnStrategy.turn(current, rotation);
    }

    public MovementStrategy movementStrategy() {
        return movementStrategy;
    }

    public EnergyStrategy energyStrategy() {
        return energyStrategy;
    }

    public TurnStrategy turnStrategy() {
        return turnStrategy;
    }
}

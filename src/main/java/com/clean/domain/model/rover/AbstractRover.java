package com.clean.domain.model.rover;

import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.geometry.Rotation;
import com.clean.domain.model.ground.GroundId;
import java.util.Objects;

public abstract class AbstractRover implements Rover{

    private final RoverId id;
    private final GroundId groundId;
    private final EnergyCapacity capacity;
    private CellPosition position;
    private Direction direction;
    private EnergyLevel energy;

    protected AbstractRover(RoverId id,
                            GroundId groundId,
                            CellPosition position,
                            Direction direction,
                            EnergyCapacity capacity,
                            EnergyLevel initialEnergy) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.groundId = Objects.requireNonNull(groundId, "groundId must not be null");
        this.position = Objects.requireNonNull(position, "position must not be null");
        this.direction = Objects.requireNonNull(direction, "direction must not be null");
        this.capacity = Objects.requireNonNull(capacity, "capacity must not be null");
        this.energy = Objects.requireNonNull(initialEnergy, "initialEnergy must not be null");
        if (!capacity.canHold(initialEnergy)) {
            throw new IllegalArgumentException("Initial energy " + initialEnergy
                    + " exceeds the rover's hardware (" + capacity + ")");
        }
    }

    protected abstract CellPosition destinationFrom(CellPosition from, Direction facing);

    protected abstract EnergyCost moveCost(CellPosition from, CellPosition to);

    protected abstract Direction turned(Direction current, Rotation rotation);

    public final void turnLeft() {
        direction = Objects.requireNonNull(turned(direction, Rotation.LEFT), "turned() returned null");
    }

    public final void turnRight(){
        direction = Objects.requireNonNull(turned(direction, Rotation.RIGHT), "turned() returned null");
    }

    public final CellPosition nextPosition() {
        return Objects.requireNonNull(destinationFrom(position, direction), "destinationFrom() returned null");
    }

    public final EnergyCost costOfNextMove() {
        return Objects.requireNonNull(moveCost(position, nextPosition()), "moveCost() returned null");
    }

    @Override
    public RoverId id() {
        return id;
    }

    @Override
    public GroundId groundId() {
        return groundId;
    }

    @Override
    public Direction direction() {
        return direction;
    }

    @Override
    public EnergyLevel energy() {
        return energy;
    }

    @Override
    public EnergyCapacity capacity() {
        return capacity;
    }

    @Override
    public CellPosition position() {
        return position;
    }

    public final EnergyCost advance() {
        CellPosition destination = nextPosition();
        EnergyCost cost = costOfNextMove();
        if (!hasEnoughFuelFor(cost)) {
            throw new IllegalStateException("Not enough fuel: need " + cost.fuel() + ", have " + energy.fuel());
        }
        if (!hasEnoughBatteryFor(cost)) {
            throw new IllegalStateException("Not enough battery: need " + cost.battery()
                    + ", have " + energy.battery());
        }
        energy = energy.minus(cost);
        position = destination;
        return cost;
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + position.x() + " " + position.y() + " " + direction
                + ", fuel=" + energy.fuel() + "/" + capacity.maxFuel()
                + ", battery=" + energy.battery() + "/" + capacity.maxBattery() + "]";
    }
}

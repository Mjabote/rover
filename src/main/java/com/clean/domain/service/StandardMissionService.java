package com.clean.domain.service;

import com.clean.domain.exception.InvalidPlacementException;
import com.clean.domain.exception.PlateauMismatchException;
import com.clean.domain.model.*;
import com.clean.domain.strategy.EnergyStrategy;
import com.clean.domain.strategy.MovementStrategy;
import com.clean.domain.strategy.TurnStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StandardMissionService {

    public Rover deployRover(Ground ground,
                             CellPosition start,
                             Direction direction,
                             EnergyCapacity capacity,
                             EnergyLevel initialEnergy) {
        Objects.requireNonNull(ground, "ground must not be null");
        validateStartingCell(ground, start);
        Rover rover = new StandardRover(RoverId.newId(), ground.id(), start, direction, capacity, initialEnergy);
        ground.markExplored(start);
        ground.placeObstacle(start);
        return rover;
    }

    public CommandResult execute(Rover rover, Ground ground, RoverCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        return switch (command) {
            case TURN_LEFT -> turnLeft(rover, ground);
            case TURN_RIGHT -> turnRight(rover, ground);
            case MOVE_FORWARD -> move(rover, ground);
        };
    }

    public List<CommandResult> executeAll(Rover rover, Ground ground, List<RoverCommand> commands) {
        List<CommandResult> results = new ArrayList<>(commands.size());
        for (RoverCommand command : commands) {
            results.add(execute(rover, ground, command));
        }
        return results;
    }

    public CommandResult turnLeft(Rover rover, Ground ground) {
        requireSameGround(rover, ground);
        Direction before = rover.direction();
        rover.turnLeft();
        return new CommandResult.Turned(before, rover.direction());
    }

    public CommandResult turnRight(Rover rover, Ground ground) {
        requireSameGround(rover, ground);
        Direction before = rover.direction();
        rover.turnRight();
        return new CommandResult.Turned(before, rover.direction());
    }

    public CommandResult move(Rover rover, Ground ground) {
        requireSameGround(rover, ground);

        CellPosition from = rover.position();
        CellPosition destination = rover.nextPosition();

        if (!ground.isWithinBounds(destination)) {
            return new CommandResult.Rejected(destination, MoveFailure.OUT_OF_BOUNDS);
        }
        if (ground.hasObstacleAt(destination)) {
            return new CommandResult.Rejected(destination, MoveFailure.OBSTACLE);
        }
        EnergyCost cost = rover.costOfNextMove();
        if (!rover.hasEnoughFuelFor(cost)) {
            return new CommandResult.Rejected(destination, MoveFailure.INSUFFICIENT_FUEL);
        }
        if (!rover.hasEnoughBatteryFor(cost)) {
            return new CommandResult.Rejected(destination, MoveFailure.INSUFFICIENT_BATTERY);
        }
        ground.removeObstacle(from);
        boolean newlyExplored = !ground.isExplored(destination);
        EnergyCost consumed = rover.advance();
        ground.markExplored(destination);
        ground.placeObstacle(destination);
        return new CommandResult.Moved(from, destination, consumed, newlyExplored);
    }

    private static void validateStartingCell(Ground ground, CellPosition start) {
        Objects.requireNonNull(start, "start must not be null");
        if (!ground.isWithinBounds(start)) {
            throw new InvalidPlacementException(start, "outside the ground");
        }
        if (ground.hasObstacleAt(start)) {
            throw new InvalidPlacementException(start, "cell contains an obstacle");
        }
    }

    private static void requireSameGround(Rover rover, Ground ground) {
        Objects.requireNonNull(rover, "rover must not be null");
        Objects.requireNonNull(ground, "ground must not be null");
        if (!rover.groundId().equals(ground.id())) {
            throw new PlateauMismatchException(rover.id(), rover.groundId(), ground.id());
        }
    }
}

package com.clean.domain.service;

import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.ground.Ground;
import com.clean.domain.model.ground.GroundIdentity;
import com.clean.domain.model.ground.Terrain;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.model.rover.RoverIdentity;
import com.clean.domain.command.RoverCommand;
import com.clean.domain.exception.InvalidPlacementException;
import com.clean.domain.exception.PlateauMismatchException;
import com.clean.domain.factory.RoverFactory;

import java.util.Objects;

public class StandardMissionService implements MissionService {

    @Override
    public <R extends Rover> R deployRover(Ground ground,
                                           CellPosition start,
                                           Direction direction,
                                           RoverFactory<R> factory) {
        Objects.requireNonNull(ground, "ground must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        validateStartingCell(ground, start);
        R rover = Objects.requireNonNull(factory.create(ground.id(), start, direction), "factory returned null");
        if (!rover.groundId().equals(ground.id()) || !rover.position().equals(start)
                || rover.direction() != direction) {
            throw new IllegalStateException("Factory built " + rover
                    + " instead of a rover at " + start + " facing " + direction + " on this ground");
        }
        ground.markExplored(start);
        ground.placeObstacle(start);
        return rover;
    }
    // command with pattern design make it extensible
    @Override
    public CommandResult execute(Rover rover, Ground ground, RoverCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        requireSameGround(rover, ground);
        return Objects.requireNonNull(command.execute(rover, ground), "command returned null");
    }

    private static void validateStartingCell(Terrain ground, CellPosition start) {
        Objects.requireNonNull(start, "start must not be null");
        if (!ground.isWithinBounds(start)) {
            throw new InvalidPlacementException(start, "outside the ground");
        }
        if (ground.hasObstacleAt(start)) {
            throw new InvalidPlacementException(start, "cell contains an obstacle");
        }
    }

    private static void requireSameGround(RoverIdentity rover, GroundIdentity ground) {
        Objects.requireNonNull(rover, "rover must not be null");
        Objects.requireNonNull(ground, "ground must not be null");
        if (!rover.groundId().equals(ground.id())) {
            throw new PlateauMismatchException(rover.id(), rover.groundId(), ground.id());
        }
    }
}

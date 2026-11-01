package com.clean.domain.command;

import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.ground.ExplorationMap;
import com.clean.domain.model.ground.Ground;
import com.clean.domain.model.ground.ObstacleMap;
import com.clean.domain.model.ground.Terrain;
import com.clean.domain.model.rover.Mobile;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.service.CommandResult;
import com.clean.domain.service.MoveFailure;

public final class MoveForwardCommand implements RoverCommand {

    @Override
    public CommandResult execute(Rover rover, Ground ground) {
        return move(rover, ground);
    }

    /** A move needs a mobile rover and a ground it can read, occupy and explore; nothing more. */
    static <G extends Terrain & ObstacleMap & ExplorationMap> CommandResult move(Mobile rover, G ground) {
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

    @Override
    public String toString() {
        return "MOVE_FORWARD";
    }
}

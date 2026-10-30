package com.clean.domain.command;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.EnergyCost;
import com.clean.domain.model.Ground;
import com.clean.domain.model.Rover;
import com.clean.domain.service.CommandResult;
import com.clean.domain.service.MoveFailure;

public final class MoveForwardCommand implements RoverCommand {

    @Override
    public CommandResult execute(Rover rover, Ground ground) {
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

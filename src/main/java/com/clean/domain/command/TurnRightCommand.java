package com.clean.domain.command;

import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.ground.Ground;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.model.rover.Steerable;
import com.clean.domain.service.CommandResult;

public final class TurnRightCommand implements RoverCommand {

    @Override
    public CommandResult execute(Rover rover, Ground ground) {
        return turn(rover);
    }

    /** A turn only needs a steerable rover; the ground is irrelevant. */
    static CommandResult turn(Steerable rover) {
        Direction before = rover.direction();
        rover.turnRight();
        return new CommandResult.Turned(before, rover.direction());
    }

    @Override
    public String toString() {
        return "TURN_RIGHT";
    }
}

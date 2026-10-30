package com.clean.domain.command;

import com.clean.domain.model.Direction;
import com.clean.domain.model.Ground;
import com.clean.domain.model.Rover;
import com.clean.domain.service.CommandResult;

public final class TurnRightCommand implements RoverCommand {

    @Override
    public CommandResult execute(Rover rover, Ground ground) {
        Direction before = rover.direction();
        rover.turnRight();
        return new CommandResult.Turned(before, rover.direction());
    }

    @Override
    public String toString() {
        return "TURN_RIGHT";
    }
}
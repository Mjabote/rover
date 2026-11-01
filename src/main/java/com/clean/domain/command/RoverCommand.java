package com.clean.domain.command;

import com.clean.domain.model.ground.Ground;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.service.CommandResult;

@FunctionalInterface
public interface RoverCommand {

    CommandResult execute(Rover rover, Ground ground);

    RoverCommand TURN_LEFT = new TurnLeftCommand();
    RoverCommand TURN_RIGHT = new TurnRightCommand();
    RoverCommand MOVE_FORWARD = new MoveForwardCommand();
}
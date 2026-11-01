package com.clean.domain.service;

import com.clean.domain.model.ground.Ground;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.command.RoverCommand;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {

    CommandResult execute(Rover rover, Ground ground, RoverCommand command);

    default List<CommandResult> executeAll(Rover rover, Ground ground, List<RoverCommand> commands) {
        List<CommandResult> results = new ArrayList<>(commands.size());
        for (RoverCommand command : commands) {
            results.add(execute(rover, ground, command));
        }
        return results;
    }
}

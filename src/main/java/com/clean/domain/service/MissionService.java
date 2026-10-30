package com.clean.domain.service;

import com.clean.domain.command.RoverCommand;
import com.clean.domain.factory.RoverFactory;
import com.clean.domain.model.*;

import java.util.ArrayList;
import java.util.List;

public interface MissionService {

    <R extends Rover> R deployRover(Ground ground, CellPosition start, Direction direction,
                                    RoverFactory<R> factory);

    /** Runs one command on a rover of this ground. */
    CommandResult execute(Rover rover, Ground ground, RoverCommand command);

    /** Runs every command in order; rejected ones are skipped and the rest continue. */
    default List<CommandResult> executeAll(Rover rover, Ground ground, List<RoverCommand> commands) {
        List<CommandResult> results = new ArrayList<>(commands.size());
        for (RoverCommand command : commands) {
            results.add(execute(rover, ground, command));
        }
        return results;
    }
}

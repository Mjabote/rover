package com.clean.application;

import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.command.RoverCommand;

import java.util.List;

public record RoverPlan(CellPosition start, Direction direction, List<RoverCommand> commands, int lineNumber) {

    public RoverPlan {
        commands = List.copyOf(commands);
    }
}
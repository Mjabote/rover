package com.clean.application;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.Direction;
import com.clean.domain.model.RoverCommand;

import java.util.List;

public record RoverPlan(CellPosition start, Direction direction, List<RoverCommand> commands, int lineNumber) {

    public RoverPlan {
        commands = List.copyOf(commands);
    }
}
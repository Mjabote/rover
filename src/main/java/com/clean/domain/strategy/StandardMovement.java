package com.clean.domain.strategy;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.Direction;

public final class StandardMovement implements MovementStrategy {

    @Override
    public CellPosition nextPosition(CellPosition current, Direction facing) {
        return current.neighbour(facing);
    }
}

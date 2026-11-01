package com.clean.domain.strategy;

import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;

public final class StandardMovement implements MovementStrategy {

    @Override
    public CellPosition nextPosition(CellPosition current, Direction facing) {
        return current.neighbour(facing);
    }
}

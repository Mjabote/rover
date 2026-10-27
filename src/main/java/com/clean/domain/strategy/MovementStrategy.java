package com.clean.domain.strategy;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.Direction;

public interface MovementStrategy {

    CellPosition nextPosition(CellPosition current, Direction facing);
}

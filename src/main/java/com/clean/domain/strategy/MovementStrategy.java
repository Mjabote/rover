package com.clean.domain.strategy;

import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;

public interface MovementStrategy {

    CellPosition nextPosition(CellPosition current, Direction facing);
}

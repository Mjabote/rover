package com.clean.domain.model.rover;

import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;

public interface RoverPose {

    CellPosition position();

    Direction direction();
}

package com.clean.domain.model.ground;

import com.clean.domain.model.geometry.CellPosition;

public interface ObstacleMap {

    void placeObstacle(CellPosition position);

    void removeObstacle(CellPosition position);
}

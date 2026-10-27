package com.clean.domain.model;

import java.util.Collection;

public interface Ground {

    GroundId id();

    CellSize cellSize();

    boolean isWithinBounds(CellPosition position);

    Cell cellAt(CellPosition position);

    Collection<Cell> cells();

    void markExplored(CellPosition position);

    void placeObstacle(CellPosition position);

    void removeObstacle(CellPosition position);

    default boolean hasObstacleAt(CellPosition position) {
        return cellAt(position).isObstacle();
    }

    default boolean isExplored(CellPosition position) {
        return cellAt(position).isExplored();
    }

    default boolean isTraversable(CellPosition position) {
        return isWithinBounds(position) && !hasObstacleAt(position);
    }

    default int totalCellCount() {
        return cells().size();
    }

    default int exploredCellCount() {
        int count = 0;
        for (Cell cell : cells()) {
            if (cell.isExplored()) {
                count++;
            }
        }
        return count;
    }

}

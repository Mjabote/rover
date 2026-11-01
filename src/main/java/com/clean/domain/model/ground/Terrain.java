package com.clean.domain.model.ground;

import com.clean.domain.model.geometry.CellPosition;
import java.util.Collection;

public interface Terrain {

    boolean isWithinBounds(CellPosition position);

    Cell cellAt(CellPosition position);

    Collection<Cell> cells();

    default boolean hasObstacleAt(CellPosition position) {
        return cellAt(position).isObstacle();
    }

    default boolean isExplored(CellPosition position) {
        return cellAt(position).isExplored();
    }

    default boolean isTraversable(CellPosition position) {
        return isWithinBounds(position) && !hasObstacleAt(position);
    }
}

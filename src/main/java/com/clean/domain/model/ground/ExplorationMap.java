package com.clean.domain.model.ground;

import com.clean.domain.model.geometry.CellPosition;

/** Records which cells of a ground have been explored. */
public interface ExplorationMap {

    void markExplored(CellPosition position);
}

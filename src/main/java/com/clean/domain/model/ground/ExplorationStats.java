package com.clean.domain.model.ground;

import java.util.Collection;

public interface ExplorationStats {

    Collection<Cell> cells();

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

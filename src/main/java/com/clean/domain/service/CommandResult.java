package com.clean.domain.service;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.Direction;
import com.clean.domain.model.EnergyCost;

public sealed interface CommandResult {

    boolean succeeded();

    record Turned(Direction from, Direction to) implements CommandResult {
        @Override
        public boolean succeeded() {
            return true;
        }
    }

    record Moved(CellPosition from, CellPosition to, EnergyCost cost, boolean newlyExplored)
            implements CommandResult {
        @Override
        public boolean succeeded() {
            return true;
        }
    }

    record Rejected(CellPosition attemptedDestination, MoveFailure reason) implements CommandResult {
        @Override
        public boolean succeeded() {
            return false;
        }
    }


}

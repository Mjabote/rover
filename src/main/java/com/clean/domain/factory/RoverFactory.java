package com.clean.domain.factory;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.Direction;
import com.clean.domain.model.GroundId;
import com.clean.domain.model.Rover;

@FunctionalInterface
public interface RoverFactory<R extends Rover> {

    /** Builds a rover standing on {@code start} of the ground {@code groundId}, facing {@code direction}. */
    R create(GroundId groundId, CellPosition start, Direction direction);
}
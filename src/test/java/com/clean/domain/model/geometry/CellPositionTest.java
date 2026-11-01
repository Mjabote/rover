package com.clean.domain.model.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellPositionTest {

    @Test
    void neighbourFollowsDirectionOffsets() {
        CellPosition origin = new CellPosition(2, 2);

        assertEquals(new CellPosition(2, 3), origin.neighbour(Direction.N));
        assertEquals(new CellPosition(3, 2), origin.neighbour(Direction.E));
        assertEquals(new CellPosition(2, 1), origin.neighbour(Direction.S));
        assertEquals(new CellPosition(1, 2), origin.neighbour(Direction.W));
    }

    @Test
    void gridDistanceIsManhattanDistance() {
        assertEquals(7, new CellPosition(1, 2).gridDistanceTo(new CellPosition(4, -2)));
    }
}

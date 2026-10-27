package com.clean.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void turningLeftFourTimesReturnsToStart() {
        for (Direction start : Direction.values()) {
            assertEquals(start, start.turnLeft().turnLeft().turnLeft().turnLeft());
        }
        assertEquals(Direction.W, Direction.N.turnLeft());
        assertEquals(Direction.E, Direction.N.turnRight());
    }

    @Test
    void fromInputRejectsUnknownCode() {
        assertEquals(Direction.S, Direction.fromInput("S"));
        assertThrows(IllegalArgumentException.class, () -> Direction.fromInput("X"));
        assertThrows(IllegalArgumentException.class, () -> Direction.fromInput(null));
    }
}

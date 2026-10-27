package com.clean.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlateauTest {

    @Test
    void fromUpperRightCornerIncludesBothCorners() {
        Plateau plateau = Plateau.fromUpperRightCorner(5, 5, CellSize.ofMetre(1));

        assertEquals(36, plateau.totalCellCount());
        assertTrue(plateau.isWithinBounds(new CellPosition(0, 0)));
        assertTrue(plateau.isWithinBounds(new CellPosition(5, 5)));
        assertFalse(plateau.isWithinBounds(new CellPosition(6, 5)));
        assertThrows(IllegalArgumentException.class,
                () -> Plateau.fromUpperRightCorner(-1, 3, CellSize.ofMetre(1)));
    }

    @Test
    void dimensionsAreReportedInCellsAndMetres() {
        Plateau plateau = Plateau.create(4, 2, CellSize.ofMetre(0.5));

        assertEquals(4, plateau.width());
        assertEquals(2, plateau.height());
        assertEquals(3, plateau.maxX());
        assertEquals(1, plateau.maxY());
        assertEquals(2.0, plateau.widthInMetres());
        assertEquals(1.0, plateau.heightInMetres());
        assertEquals("Plateau 4x2 (0, 0)..(3, 1)", plateau.toString());
        assertThrows(IllegalArgumentException.class, () -> Plateau.create(0, 2, CellSize.ofMetre(1)));
    }

    @Test
    void cellAtRejectsPositionOutsideTheGround() {
        Plateau plateau = Plateau.create(2, 2, CellSize.ofMetre(1));

        assertEquals(new CellPosition(1, 1), plateau.cellAt(new CellPosition(1, 1)).getCellPosition());
        assertThrows(IllegalArgumentException.class, () -> plateau.cellAt(new CellPosition(2, 0)));
        assertThrows(IllegalArgumentException.class, () -> plateau.markExplored(new CellPosition(-1, 0)));
    }

    @Test
    void markExploredIsCountedOnce() {
        Plateau plateau = Plateau.create(2, 2, CellSize.ofMetre(1));
        CellPosition position = new CellPosition(0, 1);

        plateau.markExplored(position);
        plateau.markExplored(position);

        assertTrue(plateau.isExplored(position));
        assertEquals(1, plateau.exploredCellCount());
        assertThrows(UnsupportedOperationException.class, () -> plateau.cells().clear());
    }

    @Test
    void obstacleMakesCellNotTraversableUntilRemoved() {
        Plateau plateau = Plateau.create(3, 3, CellSize.ofMetre(1));
        CellPosition position = new CellPosition(1, 1);

        plateau.placeObstacle(position);
        assertFalse(plateau.isTraversable(position));

        plateau.removeObstacle(position);
        assertTrue(plateau.isTraversable(position));
    }
}

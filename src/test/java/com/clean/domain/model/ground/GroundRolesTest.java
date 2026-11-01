package com.clean.domain.model.ground;

import com.clean.domain.model.geometry.CellPosition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroundRolesTest {

    @Test
    void groundIsTheSumOfItsRoles() {
        for (Class<?> role : List.of(GroundIdentity.class, Terrain.class, ObstacleMap.class,
                ExplorationMap.class, ExplorationStats.class)) {
            assertTrue(role.isAssignableFrom(Ground.class), "Ground should be a " + role.getSimpleName());
        }
    }

    @Test
    void roleInterfacesDoNotLeakUnrelatedMethods() {
        assertFalse(ObstacleMap.class.isAssignableFrom(Terrain.class));
        assertFalse(ExplorationMap.class.isAssignableFrom(ExplorationStats.class));
    }

    @Test
    void explorationStatsOnlyNeedsCells() {
        Cell explored = new Cell(new CellPosition(0, 0));
        explored.markAsExplored();
        ExplorationStats stats = () -> List.of(explored, new Cell(new CellPosition(0, 1)));

        assertEquals(2, stats.totalCellCount());
        assertEquals(1, stats.exploredCellCount());
    }

    @Test
    void terrainDefaultsWorkThroughTheReadOnlyView() {
        Plateau plateau = Plateau.create(2, 1, CellSize.ofMetre(1));
        plateau.placeObstacle(new CellPosition(1, 0));
        Terrain terrain = plateau;

        assertTrue(terrain.isTraversable(new CellPosition(0, 0)));
        assertFalse(terrain.isTraversable(new CellPosition(1, 0)));
        assertFalse(terrain.isTraversable(new CellPosition(2, 0)));
        assertFalse(terrain.isExplored(new CellPosition(0, 0)));
    }
}

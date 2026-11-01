package com.clean.domain.model.ground;

import com.clean.domain.model.geometry.CellPosition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroundValueObjectsTest {

    @Test
    void cellSizeMustBePositiveAndFinite() {
        assertEquals(2.5, CellSize.ofMetre(2.5).metres());
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(0));
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(-1));
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> CellSize.ofMetre(Double.POSITIVE_INFINITY));
    }

    @Test
    void groundIdsAreUniqueAndRejectNull() {
        assertNotEquals(GroundId.newId(), GroundId.newId());
        assertThrows(NullPointerException.class, () -> new GroundId(null));

        GroundId id = GroundId.newId();
        assertEquals(id.value().toString(), id.toString());
    }

    @Test
    void groundRejectsInvalidCellCollections() {
        CellSize size = CellSize.ofMetre(1);
        GroundId id = GroundId.newId();
        CellPosition origin = new CellPosition(0, 0);

        assertThrows(IllegalArgumentException.class, () -> new TestGround(id, size, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new TestGround(id, size, List.of(origin, origin)));
        assertEquals(1, new TestGround(id, size, List.of(origin)).totalCellCount());
    }

    private static final class TestGround extends AbstractGround {
        TestGround(GroundId id, CellSize cellSize, List<CellPosition> positions) {
            super(id, cellSize, positions);
        }
    }
}

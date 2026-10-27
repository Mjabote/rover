package com.clean.domain.model;

import java.util.Objects;

public record CellPosition(int x, int y) {


    public CellPosition neighbour(Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");
        return new CellPosition(x + direction.dx(), y + direction.dy());
    }

    public int gridDistanceTo(CellPosition other){return Math.abs(x- other.x) + Math.abs(y - other.y);}


}

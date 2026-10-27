package com.clean.domain.model;

import java.util.Objects;

public class Cell {
    private CellPosition cellPosition;
    private boolean explored;
    private boolean obstacle;

    public Cell(CellPosition cellPosition) {
        this.cellPosition =  Objects.requireNonNull(cellPosition, "position must not be null");;
    }

    public CellPosition getCellPosition() {
        return cellPosition;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public boolean isExplored() {
        return explored;
    }
    void markAsExplored() {
        this.explored = true;
    }
    void putObstacle() {
        this.obstacle = true;
    }

    void removeObstacle(){
        this.obstacle = false;
    }

}

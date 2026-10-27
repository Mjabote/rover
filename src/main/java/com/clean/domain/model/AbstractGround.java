package com.clean.domain.model;

import java.util.*;

public abstract class AbstractGround implements Ground {

    private final GroundId id;
    private final CellSize cellSize;
    private final Map<CellPosition, Cell> cells;

    protected AbstractGround(GroundId id, CellSize cellSize, Collection<CellPosition> positions){
        this.id = Objects.requireNonNull(id , "id must not be null");
        this.cellSize = Objects.requireNonNull(cellSize, "cellSize must not be null");
        Objects.requireNonNull(positions, "positions must not be null");
        if(positions.isEmpty()){
            throw new IllegalArgumentException("A ground must have at least one cell");
        }
        Map<CellPosition, Cell> map = new LinkedHashMap<>();
        for(CellPosition position: positions){
            Objects.requireNonNull(position,"positions must not contain null");
            if(map.putIfAbsent(position,new Cell(position)) != null){
                throw new IllegalArgumentException("Duplicate position " + position);
            }
        }
        this.cells = map;
    }

    @Override
    public final GroundId id() {
        return id;
    }

    @Override
    public final CellSize cellSize() {
        return cellSize;
    }

    @Override
    public final boolean isWithinBounds(CellPosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return cells.containsKey(position);
    }

    @Override
    public final Cell cellAt(CellPosition position) {
        Cell cell = cells.get(Objects.requireNonNull(position, "position must not be null"));
        if(cell == null){
            throw new IllegalArgumentException("Position " + position + " is not part of " + this);
        }
        return cell;
    }

    @Override
    public final Collection<Cell> cells() {
        return Collections.unmodifiableCollection(cells.values());
    }

    @Override
    public final void markExplored(CellPosition position) {
        cellAt(position).markAsExplored();
    }

    @Override
    public final void placeObstacle(CellPosition position) {
        cellAt(position).putObstacle();

    }

    @Override
    public final void removeObstacle(CellPosition position) {
        cellAt(position).removeObstacle();
    }
}

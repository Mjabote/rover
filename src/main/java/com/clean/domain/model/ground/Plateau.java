package com.clean.domain.model.ground;

import com.clean.domain.model.geometry.CellPosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Plateau extends AbstractGround{

    private final int width;
    private final int height;

    private Plateau(GroundId id, int width, int height, CellSize cellSize) {
        super(id, cellSize, rectangle(width, height));
        this.width = width;
        this.height = height;
    }

    public static Plateau create(int width, int height, CellSize cellSize){
        return new Plateau(GroundId.newId(), width, height, cellSize);
    }

    public static Plateau fromUpperRightCorner(int maxX, int maxY, CellSize cellSize) {
        if (maxX < 0 || maxY < 0) {
            throw new IllegalArgumentException("Upper-right coordinates must not be negative: " + maxX + " " + maxY);
        }
        if (maxX == Integer.MAX_VALUE || maxY == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Upper-right coordinates are too large: " + maxX + " " + maxY);
        }
        return create(maxX + 1, maxY + 1, cellSize);
    }

    private static List<CellPosition> rectangle(int width, int height){
        if(width<=0 || height <=0){
            throw new IllegalArgumentException("Plateau must have at least one cell in each direction: "
                    + width + " x " + height);
        }
        List<CellPosition> positions = new ArrayList<>(Math.multiplyExact(width, height));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                positions.add(new CellPosition(x, y));
            }
        }
        return positions;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int maxX() {
        return width - 1;
    }

    public int maxY() {
        return height - 1;
    }

    public double widthInMetres() {
        return width * cellSize().metres();
    }

    public double heightInMetres() {
        return height * cellSize().metres();
    }

    @Override
    public String toString() {
        return "Plateau " + width + "x" + height + " (0, 0)..(" + maxX() + ", " + maxY() + ")";
    }

}

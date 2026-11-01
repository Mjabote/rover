package com.clean.domain.model.geometry;

public enum Direction {
    N(0, 1),
    E(1, 0),
    S(0, -1),
    W(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }
    public int dy() {
        return dy;
    }

    public Direction turnLeft(){
        return switch (this){
            case N -> W;
            case W -> S;
            case S -> E;
            case E -> N;

        };
    }
    public Direction turnRight(){
        return switch (this){
            case N -> E;
            case E -> S;
            case S -> W;
            case W -> N;
        };
    }

    public static Direction fromInput(String input){
        if (input == null) {
            throw new IllegalArgumentException("Direction code must not be null");
        }
        return switch (input){
            case "N" -> N;
            case "E" -> E;
            case "W" -> W;
            case "S" -> S;
            default -> throw new IllegalArgumentException(
                    "Invalid direction '" + input + "' (expected N, E, S or W)");
        };
    }

}

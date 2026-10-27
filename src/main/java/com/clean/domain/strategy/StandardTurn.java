package com.clean.domain.strategy;

import com.clean.domain.model.Direction;
import com.clean.domain.model.Rotation;

public class StandardTurn implements TurnStrategy{

    public static final int ANGLE_DEGREES = 90;

    @Override
    public Direction turn(Direction current, Rotation rotation) {
        return switch (rotation){
            case LEFT -> current.turnLeft();
            case RIGHT -> current.turnRight();
        };
    }
}

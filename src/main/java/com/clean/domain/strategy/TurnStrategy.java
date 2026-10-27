package com.clean.domain.strategy;

import com.clean.domain.model.Direction;
import com.clean.domain.model.Rotation;

import java.time.Duration;

public interface TurnStrategy {

    Direction turn(Direction current, Rotation rotation);
}

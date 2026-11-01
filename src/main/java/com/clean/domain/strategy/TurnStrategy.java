package com.clean.domain.strategy;

import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.geometry.Rotation;
import java.time.Duration;

public interface TurnStrategy {

    Direction turn(Direction current, Rotation rotation);
}

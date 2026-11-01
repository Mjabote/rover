package com.clean.domain.model.rover;

public interface Steerable extends RoverPose {

    void turnLeft();

    void turnRight();
}

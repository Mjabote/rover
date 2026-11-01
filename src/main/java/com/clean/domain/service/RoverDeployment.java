package com.clean.domain.service;

import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.ground.Ground;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.factory.RoverFactory;

public interface RoverDeployment {

    <R extends Rover> R deployRover(Ground ground, CellPosition start, Direction direction,
                                    RoverFactory<R> factory);
}

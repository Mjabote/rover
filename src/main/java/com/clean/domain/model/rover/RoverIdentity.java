package com.clean.domain.model.rover;

import com.clean.domain.model.ground.GroundId;

public interface RoverIdentity {

    RoverId id();

    GroundId groundId();
}

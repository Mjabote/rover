package com.clean.domain.model.rover;

import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.geometry.CellPosition;

public interface Mobile extends RoverPose, PoweredRover {

    CellPosition nextPosition();

    EnergyCost costOfNextMove();

    EnergyCost advance();
}

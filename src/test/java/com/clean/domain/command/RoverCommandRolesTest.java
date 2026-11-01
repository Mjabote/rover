package com.clean.domain.command;

import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.ground.CellSize;
import com.clean.domain.model.ground.GroundId;
import com.clean.domain.model.ground.Plateau;
import com.clean.domain.model.rover.Mobile;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.model.rover.RoverId;
import com.clean.domain.model.rover.StandardRover;
import com.clean.domain.model.rover.Steerable;
import com.clean.domain.service.CommandResult;
import com.clean.domain.service.MoveFailure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoverCommandRolesTest {

    private static final class Compass implements Steerable {
        private Direction direction = Direction.N;

        @Override public CellPosition position() { return new CellPosition(0, 0); }
        @Override public Direction direction() { return direction; }
        @Override public void turnLeft() { direction = direction.turnLeft(); }
        @Override public void turnRight() { direction = direction.turnRight(); }
    }

    @Test
    void turnCommandsOnlyNeedASteerable() {
        Compass compass = new Compass();

        assertEquals(new CommandResult.Turned(Direction.N, Direction.W), TurnLeftCommand.turn(compass));
        assertEquals(new CommandResult.Turned(Direction.W, Direction.N), TurnRightCommand.turn(compass));
    }

    @Test
    void turnCommandsIgnoreTheGround() {
        Rover rover = new StandardRover(RoverId.newId(), GroundId.newId(), new CellPosition(0, 0), Direction.E,
                EnergyCapacity.of(10, 10), EnergyLevel.of(10, 10));

        assertEquals(new CommandResult.Turned(Direction.E, Direction.N), RoverCommand.TURN_LEFT.execute(rover, null));
        assertEquals(new CommandResult.Turned(Direction.N, Direction.E), RoverCommand.TURN_RIGHT.execute(rover, null));
    }

    @Test
    void moveNeedsOnlyAMobileRoverAndAnExplorableTerrain() {
        Plateau plateau = Plateau.create(2, 2, CellSize.ofMetre(1));
        Mobile rover = new StandardRover(RoverId.newId(), plateau.id(), new CellPosition(0, 0), Direction.N,
                EnergyCapacity.of(10, 10), EnergyLevel.of(10, 10));
        plateau.placeObstacle(new CellPosition(0, 0));

        CommandResult moved = MoveForwardCommand.move(rover, plateau);
        CommandResult blocked = MoveForwardCommand.move(rover, plateau);

        assertEquals(new CommandResult.Moved(new CellPosition(0, 0), new CellPosition(0, 1), EnergyCost.NONE, true),
                moved);
        assertEquals(new CommandResult.Rejected(new CellPosition(0, 2), MoveFailure.OUT_OF_BOUNDS), blocked);
        assertFalse(plateau.hasObstacleAt(new CellPosition(0, 0)));
        assertTrue(plateau.hasObstacleAt(new CellPosition(0, 1)));
        assertTrue(plateau.isExplored(new CellPosition(0, 1)));
    }
}

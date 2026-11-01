package com.clean.domain.service;

import com.clean.domain.model.energy.EnergyCapacity;
import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.energy.EnergyLevel;
import com.clean.domain.model.geometry.CellPosition;
import com.clean.domain.model.geometry.Direction;
import com.clean.domain.model.geometry.Rotation;
import com.clean.domain.model.ground.CellSize;
import com.clean.domain.model.ground.Plateau;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.model.rover.RoverId;
import com.clean.domain.model.rover.StandardRover;
import com.clean.domain.command.RoverCommand;
import com.clean.domain.exception.InvalidPlacementException;
import com.clean.domain.exception.PlateauMismatchException;
import com.clean.domain.factory.StandardRoverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardMissionServiceTest {

    private StandardMissionService service;
    private Plateau plateau;

    @BeforeEach
    void setUp() {
        service = new StandardMissionService();
        plateau = Plateau.fromUpperRightCorner(5, 5, CellSize.ofMetre(1));
    }

    private Rover deploy(int x, int y, Direction direction) {
        return service.deployRover(plateau, new CellPosition(x, y), direction,
                new StandardRoverFactory(EnergyCapacity.of(100, 100), EnergyLevel.of(100, 100)));
    }

    @Test
    void deployRoverMarksStartExploredAndOccupied() {
        Rover rover = deploy(1, 2, Direction.N);

        assertEquals(plateau.id(), rover.groundId());
        assertTrue(plateau.isExplored(new CellPosition(1, 2)));
        assertTrue(plateau.hasObstacleAt(new CellPosition(1, 2)));
    }

    @Test
    void deployRoverRejectsOutOfBoundsOrOccupiedStart() {
        assertThrows(InvalidPlacementException.class, () -> deploy(6, 0, Direction.N));

        deploy(0, 0, Direction.N);
        assertThrows(InvalidPlacementException.class, () -> deploy(0, 0, Direction.E));
    }

    @Test
    void executeTurnCommandsReportsDirections() {
        Rover rover = deploy(0, 0, Direction.N);

        assertEquals(new CommandResult.Turned(Direction.N, Direction.W),
                service.execute(rover, plateau, RoverCommand.TURN_LEFT));
        assertEquals(new CommandResult.Turned(Direction.W, Direction.N),
                service.execute(rover, plateau, RoverCommand.TURN_RIGHT));
    }

    @Test
    void moveFreesOldCellAndOccupiesNewOne() {
        Rover rover = deploy(1, 1, Direction.E);

        CommandResult result = service.execute(rover, plateau, RoverCommand.MOVE_FORWARD);

        assertEquals(new CommandResult.Moved(new CellPosition(1, 1), new CellPosition(2, 1), EnergyCost.NONE, true),
                result);
        assertTrue(result.succeeded());
        assertFalse(plateau.hasObstacleAt(new CellPosition(1, 1)));
        assertTrue(plateau.hasObstacleAt(new CellPosition(2, 1)));
        assertEquals(2, plateau.exploredCellCount());
    }

    @Test
    void moveIntoAlreadyExploredCellIsNotNewlyExplored() {
        Rover rover = deploy(1, 1, Direction.E);
        service.executeAll(rover, plateau, List.of(
                RoverCommand.MOVE_FORWARD, RoverCommand.TURN_LEFT, RoverCommand.TURN_LEFT));

        CommandResult result = service.execute(rover, plateau, RoverCommand.MOVE_FORWARD);

        assertInstanceOf(CommandResult.Moved.class, result);
        assertFalse(((CommandResult.Moved) result).newlyExplored());
    }

    @Test
    void moveOffTheEdgeIsRejectedAsOutOfBounds() {
        Rover rover = deploy(0, 0, Direction.S);

        CommandResult result = service.execute(rover, plateau, RoverCommand.MOVE_FORWARD);

        assertEquals(new CommandResult.Rejected(new CellPosition(0, -1), MoveFailure.OUT_OF_BOUNDS), result);
        assertFalse(result.succeeded());
        assertEquals(new CellPosition(0, 0), rover.position());
    }

    @Test
    void moveIntoObstacleIsRejected() {
        Rover rover = deploy(0, 0, Direction.N);
        plateau.placeObstacle(new CellPosition(0, 1));

        CommandResult result = service.execute(rover, plateau, RoverCommand.MOVE_FORWARD);

        assertEquals(MoveFailure.OBSTACLE, ((CommandResult.Rejected) result).reason());
    }

    @Test
    void roversBlockEachOther() {
        Rover first = deploy(0, 0, Direction.N);
        deploy(0, 1, Direction.N);

        CommandResult result = service.execute(first, plateau, RoverCommand.MOVE_FORWARD);

        assertEquals(MoveFailure.OBSTACLE, ((CommandResult.Rejected) result).reason());
    }

    @Test
    void moveIsRejectedWhenFuelOrBatteryIsInsufficient() {
        Rover noFuel = costlyRover(new CellPosition(0, 0), EnergyLevel.of(5, 100));
        Rover noBattery = costlyRover(new CellPosition(3, 3), EnergyLevel.of(100, 5));

        assertEquals(MoveFailure.INSUFFICIENT_FUEL,
                ((CommandResult.Rejected) service.execute(noFuel, plateau, RoverCommand.MOVE_FORWARD)).reason());
        assertEquals(MoveFailure.INSUFFICIENT_BATTERY,
                ((CommandResult.Rejected) service.execute(noBattery, plateau, RoverCommand.MOVE_FORWARD)).reason());
    }

    @Test
    void commandsOnAnotherGroundAreRejected() {
        Rover rover = deploy(0, 0, Direction.N);
        Plateau other = Plateau.create(3, 3, CellSize.ofMetre(1));

        assertThrows(PlateauMismatchException.class, () -> service.execute(rover, other, RoverCommand.MOVE_FORWARD));
        assertThrows(PlateauMismatchException.class, () -> service.execute(rover, other, RoverCommand.TURN_LEFT));
    }

    @Test
    void failureReasonsHaveDescriptions() {
        for (MoveFailure failure : MoveFailure.values()) {
            assertFalse(failure.description().isBlank());
        }
    }

    private Rover costlyRover(CellPosition start, EnergyLevel energy) {
        return new StandardRover(RoverId.newId(), plateau.id(), start, Direction.N,
                EnergyCapacity.of(100, 100), energy,
                (from, facing) -> from.neighbour(facing),
                (from, to) -> EnergyCost.of(10, 10),
                (current, rotation) -> rotation == Rotation.LEFT ? current.turnLeft() : current.turnRight());
    }
}

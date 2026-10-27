package com.clean.application;

import com.clean.domain.exception.InvalidPlacementException;
import com.clean.domain.model.CellSize;
import com.clean.domain.model.EnergyCapacity;
import com.clean.domain.model.Plateau;
import com.clean.domain.model.Rover;
import com.clean.domain.service.StandardMissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlateauMissionRunner {
    static final CellSize DEFAULT_CELL_SIZE = CellSize.ofMetre(1.0);
    static final EnergyCapacity CLASSIC_HARDWARE = EnergyCapacity.of(1_000, 1_000);

    private final PlateauMissionFileParser parser = new PlateauMissionFileParser();
    private final StandardMissionService missionService = new StandardMissionService();

    public void run(List<String> lines, Consumer<String> output) {
        MissionPlan plan = parser.parse(lines);
        Plateau plateau = Plateau.fromUpperRightCorner(plan.maxX(), plan.maxY(), DEFAULT_CELL_SIZE);

        for (RoverPlan roverPlan : plan.rovers()) {
            if (!plateau.isWithinBounds(roverPlan.start())) {
                throw new MissionInputException(roverPlan.lineNumber(),
                        "start position " + roverPlan.start() + " is outside the plateau (0, 0)..("
                                + plateau.maxX() + ", " + plateau.maxY() + ")");
            }
        }

        for (RoverPlan roverPlan : plan.rovers()) {
            Rover rover;
            try {
                rover = missionService.deployRover(plateau, roverPlan.start(), roverPlan.direction(),
                        CLASSIC_HARDWARE, CLASSIC_HARDWARE.full());
            } catch (InvalidPlacementException e) {
                throw new MissionInputException(roverPlan.lineNumber(), e.getMessage());
            }
            missionService.executeAll(rover, plateau, roverPlan.commands());
            output.accept(format(rover));
        }
    }

    public List<String> run(List<String> lines) {
        List<String> out = new ArrayList<>();
        run(lines, out::add);
        return out;
    }

    static String format(Rover rover) {
        return rover.position().x() + " " + rover.position().y() + " " + rover.direction().name();
    }
}

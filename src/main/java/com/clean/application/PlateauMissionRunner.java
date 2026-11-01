package com.clean.application;

import com.clean.domain.model.ground.CellSize;
import com.clean.domain.model.ground.ExplorationStats;
import com.clean.domain.model.ground.Plateau;
import com.clean.domain.model.rover.Rover;
import com.clean.domain.model.rover.RoverPose;
import com.clean.domain.exception.InvalidPlacementException;
import com.clean.domain.factory.RoverFactory;
import com.clean.domain.service.CommandResult;
import com.clean.domain.service.MissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class PlateauMissionRunner {
    private static final Logger log = LoggerFactory.getLogger(PlateauMissionRunner.class);

    static final CellSize DEFAULT_CELL_SIZE = CellSize.ofMetre(1.0);
    private final PlateauMissionFileParser parser;
    private final MissionService missionService;

    public PlateauMissionRunner(InstructionParser instructions, MissionService missionService) {
        this.parser = new PlateauMissionFileParser(instructions);
        this.missionService = missionService;
    }

    public void run(List<String> lines, RoverFactory factory,Consumer<String> output) {
        MissionPlan plan = parser.parse(lines);
        Plateau plateau = Plateau.fromUpperRightCorner(plan.maxX(), plan.maxY(), DEFAULT_CELL_SIZE);
        log.info("Starting mission on {} with {} rover(s)", plateau, plan.rovers().size());

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
                        factory);
            } catch (InvalidPlacementException e) {
                throw new MissionInputException(roverPlan.lineNumber(), e.getMessage());
            }
            log.info("Deployed rover {} at {} facing {} (line {})",
                    rover.id(), roverPlan.start(), roverPlan.direction(), roverPlan.lineNumber());

            List<CommandResult> results = missionService.executeAll(rover, plateau, roverPlan.commands());
            logResults(rover, roverPlan, results);
            output.accept(format(rover));
        }
        logCompletion(plateau);
    }

    private static void logCompletion(ExplorationStats stats) {
        log.info("Mission complete: {}/{} cells explored", stats.exploredCellCount(), stats.totalCellCount());
    }

    private static void logResults(Rover rover, RoverPlan roverPlan, List<CommandResult> results) {
        int rejected = 0;
        for (int i = 0; i < results.size(); i++) {
            CommandResult result = results.get(i);
            log.debug("Rover {} command #{} {} -> {}", rover.id(), i + 1, roverPlan.commands().get(i), result);
            if (result instanceof CommandResult.Rejected(var destination, var reason)) {
                rejected++;
                log.warn("Rover {} (line {}): move #{} to {} rejected, {}",
                        rover.id(), roverPlan.lineNumber(), i + 1, destination, reason.description());
            }
        }
        log.info("Rover {} finished at {} facing {} ({} command(s), {} rejected)",
                rover.id(), rover.position(), rover.direction(), results.size(), rejected);
    }

    static String format(RoverPose rover) {
        return rover.position().x() + " " + rover.position().y() + " " + rover.direction().name();
    }
}

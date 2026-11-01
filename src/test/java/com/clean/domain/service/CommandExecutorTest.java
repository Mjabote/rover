package com.clean.domain.service;

import com.clean.domain.model.geometry.Direction;
import com.clean.domain.command.RoverCommand;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorTest {

    @Test
    void missionServiceCombinesDeploymentAndExecution() {
        assertTrue(RoverDeployment.class.isAssignableFrom(MissionService.class));
        assertTrue(CommandExecutor.class.isAssignableFrom(MissionService.class));
        assertFalse(RoverDeployment.class.isAssignableFrom(CommandExecutor.class));
    }

    @Test
    void executeAllRunsEveryCommandInOrder() {
        List<RoverCommand> seen = new ArrayList<>();
        CommandExecutor executor = (rover, ground, command) -> {
            seen.add(command);
            return new CommandResult.Turned(Direction.N, Direction.N);
        };
        List<RoverCommand> commands = List.of(RoverCommand.TURN_LEFT, RoverCommand.MOVE_FORWARD, RoverCommand.TURN_RIGHT);

        List<CommandResult> results = executor.executeAll(null, null, commands);

        assertEquals(commands, seen);
        assertEquals(3, results.size());
    }
}

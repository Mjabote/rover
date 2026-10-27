package com.clean.application;

import com.clean.domain.model.RoverCommand;

import java.util.ArrayList;
import java.util.List;

public final class InstructionParser {

    private InstructionParser() {
    }

    public static RoverCommand toCommand(char letter) {
        return switch (letter) {
            case 'L' -> RoverCommand.TURN_LEFT;
            case 'R' -> RoverCommand.TURN_RIGHT;
            case 'M' -> RoverCommand.MOVE_FORWARD;
            default -> throw new IllegalArgumentException(
                    "Invalid instruction '" + letter + "' (expected L, R or M)");
        };
    }

    public static List<RoverCommand> parse(String instructions) {
        List<RoverCommand> commands = new ArrayList<>(instructions.length());
        for (int i = 0; i < instructions.length(); i++) {
            commands.add(toCommand(instructions.charAt(i)));
        }
        return commands;
    }
}
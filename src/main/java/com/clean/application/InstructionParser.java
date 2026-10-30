package com.clean.application;

import com.clean.domain.command.RoverCommand;

import java.util.*;

public final class InstructionParser {

    private final Map<Character, RoverCommand> commands;

    private InstructionParser(Map<Character, RoverCommand> commands) {
        this.commands = Collections.unmodifiableMap(new LinkedHashMap<>(commands));
    }

    /** The classic challenge: L, R and M. */
    public static InstructionParser standard() {
        Map<Character, RoverCommand> commands = new LinkedHashMap<>();
        commands.put('L', RoverCommand.TURN_LEFT);
        commands.put('R', RoverCommand.TURN_RIGHT);
        commands.put('M', RoverCommand.MOVE_FORWARD);
        return new InstructionParser(commands);
    }

    /** A new parser that also understands {@code letter}. */
    public InstructionParser withCommand(char letter, RoverCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        if (Character.isWhitespace(letter)) {
            throw new IllegalArgumentException("A command letter cannot be whitespace");
        }
        if (commands.containsKey(letter)) {
            throw new IllegalArgumentException("Letter '" + letter + "' is already used by " + commands.get(letter));
        }
        Map<Character, RoverCommand> extended = new LinkedHashMap<>(commands);
        extended.put(letter, command);
        return new InstructionParser(extended);
    }

    public RoverCommand toCommand(char letter) {
        RoverCommand command = commands.get(letter);
        if (command == null) {
            throw new IllegalArgumentException("Invalid instruction '" + letter + "' (expected one of "
                    + commands.keySet() + ")");
        }
        return command;
    }

    public List<RoverCommand> parse(String instructions) {
        List<RoverCommand> result = new ArrayList<>(instructions.length());
        for (int i = 0; i < instructions.length(); i++) {
            result.add(toCommand(instructions.charAt(i)));
        }
        return result;
    }

    /** The letters this parser understands, in registration order. */
    public Set<Character> letters() {
        return commands.keySet();
    }
}
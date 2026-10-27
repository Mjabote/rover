package com.clean.application;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.Direction;
import com.clean.domain.model.RoverCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlateauMissionFileParser {
    private static final Pattern PLATEAU_LINE = Pattern.compile("(\\d+)\\s+(\\d+)");
    private static final Pattern POSITION_LINE = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\S+)");

    public MissionPlan parse(List<String> rawLines) {
        List<String> lines = withoutTrailingBlankLines(rawLines);
        if (lines.isEmpty()) {
            throw new MissionInputException("Input is empty: expected plateau coordinates 'maxX maxY'");
        }

        Matcher plateau = PLATEAU_LINE.matcher(lines.get(0).strip());
        if (!plateau.matches()) {
            throw new MissionInputException(1,
                    "expected plateau coordinates 'maxX maxY', got '" + lines.get(0) + "'");
        }
        int maxX = toInt(plateau.group(1), 1);
        int maxY = toInt(plateau.group(2), 1);

        if ((lines.size() - 1) % 2 != 0 && rawLines.size() > lines.size()) {
            lines = rawLines.subList(0, lines.size() + 1);
        }
        if ((lines.size() - 1) % 2 != 0) {
            throw new MissionInputException(lines.size(),
                    "rover position has no instruction line after it");
        }

        List<RoverPlan> rovers = new ArrayList<>();
        for (int i = 1; i < lines.size(); i += 2) {
            int positionLine = i + 1;
            int instructionLine = i + 2;
            rovers.add(parseRover(lines.get(i), positionLine, lines.get(i + 1), instructionLine));
        }
        return new MissionPlan(maxX, maxY, rovers);
    }

    private RoverPlan parseRover(String positionText, int positionLine,
                                 String instructionText, int instructionLine) {
        Matcher m = POSITION_LINE.matcher(positionText.strip());
        if (!m.matches()) {
            throw new MissionInputException(positionLine,
                    "expected rover position 'x y D', got '" + positionText + "'");
        }
        CellPosition start = new CellPosition(toInt(m.group(1), positionLine), toInt(m.group(2), positionLine));

        Direction direction;
        try {
            direction = Direction.fromInput(m.group(3));
        } catch (IllegalArgumentException e) {
            throw new MissionInputException(positionLine, e.getMessage());
        }

        List<RoverCommand> commands;
        try {
            commands = InstructionParser.parse(instructionText.strip());
        } catch (IllegalArgumentException e) {
            throw new MissionInputException(instructionLine, e.getMessage());
        }
        return new RoverPlan(start, direction, commands, positionLine);
    }

    private static int toInt(String digits, int lineNumber) {
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            throw new MissionInputException(lineNumber, "number is too large: " + digits);
        }
    }
    private static List<String> withoutTrailingBlankLines(List<String> lines) {
        int end = lines.size();
        while (end > 0 && lines.get(end - 1).isBlank()) {
            end--;
        }
        return lines.subList(0, end);
    }
}
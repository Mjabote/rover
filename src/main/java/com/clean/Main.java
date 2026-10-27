package com.clean;

import com.clean.application.MissionInputException;
import com.clean.application.PlateauMissionRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static final int EXIT_OK = 0;
    static final int EXIT_INPUT_ERROR = 1;
    static final int EXIT_USAGE = 2;

    private Main() {
    }

    public static void main(String[] args) {
        int status = run(args, System.out, System.err);
        if (status != EXIT_OK) {
            System.exit(status);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 1 || args[0].isBlank()) {
            err.println("Usage: java -jar rover.jar <input-file>");
            err.println("       java -jar rover.jar --demo");
            return EXIT_USAGE;
        }

        Path inputFile = Path.of(args[0]);
        try {
            List<String> lines = Files.readAllLines(inputFile, StandardCharsets.UTF_8);
            new PlateauMissionRunner().run(lines, out::println);
            out.flush();
            return EXIT_OK;
        } catch (NoSuchFileException e) {
            err.println("Error: input file not found: " + inputFile);
        } catch (IOException e) {
            err.println("Error: cannot read input file " + inputFile + ": " + e.getMessage());
        } catch (MissionInputException e) {
            err.println("Error: " + e.getMessage());
        }
        return EXIT_INPUT_ERROR;
    }
}

package com.clean;

import com.clean.application.MissionInputException;
import com.clean.application.PlateauMissionRunner;
import com.clean.config.ApplicationContext;
import com.clean.config.BeanFactory;
import com.clean.config.BeansException;
import com.clean.config.PropertyResolver;
import com.clean.config.YamlApplicationContext;
import com.clean.domain.factory.RoverCatalog;
import com.clean.domain.factory.RoverFactory;
import com.clean.domain.factory.RoverFactoryType;
import com.clean.domain.factory.StandardRoverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(Main.class);

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
    static final String DEFAULT_CONFIG = "application.yml";

    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 1 || args[0].isBlank()) {
            err.println("Usage: java -jar rover.jar <input-file>");
            err.println("       java -jar rover.jar --demo");
            return EXIT_USAGE;
        }

        Path inputFile = Path.of(args[0]);
        try {
            ApplicationContext context = YamlApplicationContext.fromClasspath(DEFAULT_CONFIG);

            PlateauMissionRunner runner = context.getBean(PlateauMissionRunner.class);
            RoverFactory<?> factory = roverFactory(context, context);

            List<String> lines = Files.readAllLines(inputFile, StandardCharsets.UTF_8);
            log.info("Read {} line(s) from {}", lines.size(), inputFile.toAbsolutePath());
            runner.run(lines, factory, out::println);
            out.flush();
            return EXIT_OK;
        } catch (NoSuchFileException e) {
            err.println("Error: input file not found: " + inputFile);
        } catch (IOException e) {
            log.debug("Failed to read {}", inputFile, e);
            err.println("Error: cannot read input file " + inputFile + ": " + e.getMessage());
        } catch (BeansException e) {
            log.debug("Invalid configuration", e);
            err.println("Error: invalid configuration: " + e.getMessage());
        } catch (MissionInputException e) {
            log.debug("Rejected mission input from {}", inputFile, e);
            err.println("Error: " + e.getMessage());
        }
        return EXIT_INPUT_ERROR;
    }

    /** The catalogue entry chosen by the property mission.rover-type. */
    private static RoverFactory<?> roverFactory(PropertyResolver properties, BeanFactory beans) {
        String name = properties.getRequiredProperty("mission.rover-type").strip();
        try {
            return beans.getBean(RoverCatalog.class).factoryFor(RoverFactoryType.valueOf(name));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new BeansException("mission.rover-type=" + name + ": " + e.getMessage(), e);
        }
    }
}

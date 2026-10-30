# Mars Rover

## The exercise

A squad of rovers lands on a rectangular plateau on Mars. The plateau is a grid and a rover's
position is `x y D`: its coordinates plus the direction it faces (`N`, `E`, `S`, `W`).

The rovers are driven by a string of letters:

| Letter | Action                                          |
|--------|-------------------------------------------------|
| `L`    | turn 90° left, stay on the same cell            |
| `R`    | turn 90° right, stay on the same cell           |
| `M`    | move forward one cell in the direction it faces |

`(0, 0)` is the bottom-left corner. North is `y + 1`.

**Input** (`input.txt`)

```
5 5          <- upper-right corner of the plateau
1 2 N        <- rover 1: start position
LMLMLMLMM    <- rover 1: instructions
3 3 E        <- rover 2: start position
MMRMMRMRRM   <- rover 2: instructions
```

Rovers move one after the other: a rover finishes all its instructions before the next one starts.

**Output**: the final position of each rover.

```
1 3 N
5 1 E
```

### Rules added in this implementation

- A move that would leave the plateau or hit another rover is **rejected** (logged as a warning);
  the rover stays put and goes on with its next instruction.
- Every cell a rover visits is marked as **explored**.
- Rovers carry **fuel (mL)** and **battery (mAh)**. A move that the rover cannot pay for is rejected.
  The standard rover's moves cost nothing for now.

## Run it

Requires Java 25 and Maven.

```bash
mvn test                                                        # unit tests + JaCoCo report
mvn compile exec:java -Dexec.mainClass=com.clean.Main -Dexec.args=input.txt
```

Exit codes: `0` ok, `1` bad input or configuration, `2` wrong usage.

## Packages

```
com.clean
├── Main          entry point: loads the config, reads the file, prints the results
├── config        a tiny Spring-like container that builds objects from application.yml
├── application   reads the input file and runs the mission
└── domain        the business rules, no I/O, no framework
    ├── model     the plateau, its cells and the rovers
    ├── command   what a rover can do (L, R, M)
    ├── strategy  how a rover moves, turns and spends energy
    ├── factory   how rovers are built
    ├── service   deploy a rover and execute commands
    └── exception domain errors
```

Dependencies only point inward: `Main → config / application → domain`.

### `domain.model`

| Class                                    | Role                                                                |
|------------------------------------------|---------------------------------------------------------------------|
| `Ground` / `AbstractGround`              | Any terrain made of cells: bounds, obstacles, explored cells.       |
| `Plateau`                                | The rectangular ground of the exercise.                             |
| `Cell`, `CellPosition`, `CellSize`       | One grid cell, its `(x, y)` and its size in metres.                 |
| `Rover` / `AbstractRover`                | Any rover: position, direction, energy, `turnLeft/Right`, `advance`.|
| `StandardRover`                          | The rover of the exercise, its behaviour given by strategies.       |
| `Direction`, `Rotation`                  | `N E S W` with left/right turns; `LEFT` / `RIGHT`.                  |
| `Fuel`, `Charge`                         | Fuel in mL, battery in mAh (never negative).                        |
| `EnergyLevel`, `EnergyCapacity`, `EnergyCost` | What a rover has, can hold, and must pay for a move.           |
| `RoverId`, `GroundId`                    | Identifiers.                                                        |

### `domain.command` — Command pattern

`RoverCommand` is a single method `execute(rover, ground)`. `TurnLeftCommand`, `TurnRightCommand`
and `MoveForwardCommand` implement it. `MoveForwardCommand` checks bounds, obstacles, fuel and battery
before moving. To add an action, write a new `RoverCommand`; nothing else changes.

### `domain.strategy` — Strategy pattern

`MovementStrategy`, `TurnStrategy` and `EnergyStrategy` decide where a rover goes, how it turns and
what a move costs. `StandardMovement`, `StandardTurn` and `StandardEnergy` (free moves) are the defaults.
A new kind of rover is just a new combination of strategies.

### `domain.factory` — Factory pattern

`RoverFactory` builds a rover at a start position. `StandardRoverFactory` builds a `StandardRover`
with a given capacity and initial energy. `RoverCatalog` maps a `RoverFactoryType` (`STANDARD`)
to its factory.

### `domain.service`

| Class                    | Role                                                                    |
|--------------------------|-------------------------------------------------------------------------|
| `MissionService`         | Deploys a rover on a ground and executes commands.                      |
| `StandardMissionService` | Checks the start cell is free and that the rover is on the right ground.|
| `CommandResult`          | `Turned`, `Moved` or `Rejected` (sealed interface).                     |
| `MoveFailure`            | Why a move was rejected: out of bounds, obstacle, no fuel, no battery.  |

### `domain.exception`

`InvalidPlacementException` (start cell outside or occupied), `PlateauMismatchException`
(rover used on another ground).

### `application`

| Class                      | Role                                                               |
|----------------------------|--------------------------------------------------------------------|
| `PlateauMissionFileParser` | Turns the lines of the file into a `MissionPlan`, with line numbers in errors. |
| `InstructionParser`        | Maps letters to commands (`L R M`); `withCommand` adds a new letter. |
| `MissionPlan`, `RoverPlan` | The parsed plateau and, for each rover, its start and commands.    |
| `PlateauMissionRunner`     | Builds the plateau, deploys each rover, runs it, prints `x y D`.   |
| `MissionInputException`    | Any invalid input, reported as `Line n: ...`.                      |

### `config` — mini dependency injection

`YamlApplicationContext` reads `src/main/resources/application.yml` and creates every bean at startup,
like Spring:

- `class` + `args` → constructor, `factory-method` → static method, `factory-bean` → method on another bean
- `{ ref: name }` → another bean, `${key}` → a property, `calls` → methods called after creation
- properties can be overridden with `-Dkey=value` or an environment variable (`MISSION_ROVER_TYPE`)

`BeanReflection` does the reflection, `BeansException` reports configuration errors.

**Example** — change the rover's tank in `application.yml`, no code change:

```yaml
properties:
  hardware:
    standard:
      fuel-ml: 2000
      battery-mah: 500
```

## Tests

`src/test/java/com/clean/domain` covers the model (positions, directions, energy, plateau, rover)
and `StandardMissionService`, with JUnit 5.

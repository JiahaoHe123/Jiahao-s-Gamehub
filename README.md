# Game Hub

Game Hub is a Java desktop application that hosts multiple games in one unified Swing app.
It currently includes:

- Sudoku
- Snake

The project focuses on clean modular structure, maintainable gameplay logic, and automated validation with unit tests and CI.

## Highlights

- Multi-game desktop platform with module-level navigation
- MVC-style organization across model, view, and controller packages
- Shared app theming with module-specific visual settings
- Sudoku features: puzzle generation, hints, notes mode, timer, and persistent records
- Snake features: real-time game loop, countdown flow, collision handling, and persistent records
- Automated test suite (60+ unit tests) with GitHub Actions headless CI

## Tech Stack

- Java (Maven project)
- Swing (desktop UI)
- JUnit 4 (testing)
- Mockito (test doubles)
- GitHub Actions (CI)

## Project Structure

```text
src/main/java/gamehub/
	Main.java
	view/                  # app shell/home
	model/                 # shared app-level theme/record abstractions
	snake/
		model/
		view/
		controller/
	sudoku/
		model/
		view/
		controller/
		util/

src/test/java/gamehub/
	snake/
	sudoku/
```

## Features

### Sudoku

- Randomized backtracking puzzle generation
- Difficulty presets (`EASY`, `MEDIUM`, `HARD`, `NIGHTMARE`)
- Uniqueness-aware board creation workflow
- Keyboard input handling with correctness validation
- Notes mode (candidate annotations)
- Quota-based hint system by difficulty
- In-game timer and best-time tracking per difficulty
- Persistent wins/losses and statistics

### Snake

- Timer-driven game loop
- Countdown before play begins
- Direction queue and collision detection
- Food spawning logic that avoids snake body overlap
- Difficulty settings that control update speed
- Board-size presets
- Visual customization options
- Persistent best scores across difficulty and board-size combinations

## Architecture Notes

- Game Hub shell uses card-based routing to switch between modules.
- Each module has its own model/view/controller organization.
- Controllers own gameplay/state transitions.
- Views own rendering and user interaction orchestration.
- Records are persisted to local files using defensive I/O behavior.

## Requirements

- macOS, Linux, or Windows
- Java JDK 23 (project configured with `maven.compiler.release=23`)
- Maven 3.8+

Check installed versions:

```bash
java -version
mvn -v
```

## Build and Run

From the project root:

```bash
mvn clean compile
mvn exec:java
```

## Run Tests

```bash
mvn test
```

For headless environments (CI/server):

```bash
mvn -Djava.awt.headless=true test
```

## Continuous Integration

CI is configured with GitHub Actions and runs tests on push and pull requests.

Workflow file:

- `.github/workflows/build.yml`

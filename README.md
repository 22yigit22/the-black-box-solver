# The Black Box Game — Java Solver

A command-line deduction assistant for the classic **Black Box** puzzle. Four hidden balls occupy distinct squares on an **8 × 8** board. Rays enter from one of **32 edge ports**; their observed paths reveal information about the hidden configuration.

This project models ray behavior and recommends the next probe using **Shannon information entropy**. It is a solver assistant, **not a graphical game**: the user must observe a ray outcome from a separate Black Box board or simulation and enter it in the console.

## Features

- Enumerates all **635,376** distinct arrangements of four balls on 64 squares (`C(64, 4)`).
- Simulates absorbed rays, reflections, and sideways deflections.
- Filters arrangements based on user-provided observations.
- Chooses subsequent entry ports to maximize expected information gain.
- Detects invalid input and contradictory observations.
- Reports when the board is uniquely identified, observations are inconsistent, or multiple arrangements remain indistinguishable.

## Requirements

- **JDK 17 or newer**
- No third-party runtime dependencies or build-tool installation required

## Run (macOS / Linux)

From the repository root:

```bash
mkdir -p build
javac --release 17 -d build $(find src -name '*.java')
java -cp build Game
```

In IntelliJ IDEA, open the root directory as a Java project, mark `src` as a Sources Root if needed, select JDK 17+, and run `Game`.

## How to use

1. The solver prints a randomly selected edge port for the first shot.
2. Fire a ray from that numbered port in the puzzle you are solving.
3. Enter the observed result in the console:
   - `H` — the ray was **absorbed**.
   - The **same** port number — the ray was **reflected**.
   - Another port number from `1` to `32` — the ray **exited** there.
4. Repeat with the recommended next port until a unique arrangement is found or no useful ports remain.

Edge numbering is clockwise: **top 1–8** left to right, **right 9–16** top to bottom, **bottom 17–24** right to left, and **left 25–32** bottom to top.

A solved board prints `*` for hidden balls and `.` for empty squares; the top-left square is `A1` and bottom-right is `H8`.

## Algorithm

For every candidate board and every available entry port, the ray simulator predicts an exit port (`1`–`32`) or an absorption (`H`). The solver builds an outcome distribution for each port and chooses the port with the greatest Shannon entropy:

`H(X) = -sum(p(x) * log2(p(x)))`

After the user supplies an observed outcome, incompatible boards are discarded. Paired entry/exit ports are treated as reciprocal observations. The first probe is selected randomly because the untouched square board is symmetric.

**Important limitation:** Full observation does not always identify a single board. In an exhaustive check of all 635,376 configurations, only 633,040 distinct 32-ray signatures exist; some different boards are observationally equivalent. The program distinguishes this condition from inconsistent observations.

## Tests

Self-contained regression tests run on the JDK without additional libraries:

```bash
mkdir -p build
javac --release 17 -d build $(find src test -name '*.java')
java -cp build BlackBoxRegressionTest
```

GitHub Actions runs the same tests on push and pull requests (`.github/workflows/ci.yml`).

## Structure

```text
src/
  Game.java            CLI, candidate filtering, entropy-based probe selection
  BlackBox.java        Board and ball occupancy
  BlackBoxStream.java  Candidate configuration enumeration
  Laser.java           Ray simulation
  Location.java        Board coordinates
  EdgeLocation.java    Edge ports and directions
  Direction.java       Cardinal direction helpers
test/
  BlackBoxRegressionTest.java
.github/workflows/ci.yml
```

For this small, self-contained project, classes use Java's default package so source and test files can appear directly within `src/` and `test/`. Larger projects usually benefit from named packages and matching directory paths.

## Possible extensions

- Separate the console UI from a testable `Solver` service.
- Add a visualization of the 8 × 8 board and ray trajectories.
- Improve candidate-generation and probe-scoring performance for large search spaces.
- Add JUnit tests and a Maven or Gradle build if the project grows.
- Explore equivalence classes of indistinguishable board layouts.

## Source ownership and licensing

Before publishing, verify that you have the right to distribute all included source code, particularly if any starter files originated from coursework or another author. No open-source license has been added automatically; choose a license only after confirming ownership and any applicable course rules.

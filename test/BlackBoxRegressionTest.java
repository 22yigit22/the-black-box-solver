import java.util.Iterator;
import java.util.Objects;

/** Self-contained regression tests, runnable with plain javac/java. */
public final class BlackBoxRegressionTest {
    private static int assertions;

    public static void main(String[] args) {
        checkEquals("A1", Location.locationFromString("A1").toString(), "top-left coordinate");
        checkEquals("H8", Location.locationFromString("H8").toString(), "bottom-right coordinate");
        checkEquals(7, Location.locationFromString("A8").getN(), "linear position");
        expectIllegalCoordinate("A9");
        expectIllegalCoordinate("B0");
        expectIllegalCoordinate("AA");
        expectIllegalCoordinate(null);

        checkEquals(635_376L, BlackBoxStream.streamBlackBoxes().count(), "candidate count");
        checkEquals(null, EdgeLocation.getEdgeLocationWithId(0), "invalid port zero");
        checkEquals(null, EdgeLocation.getEdgeLocationWithId(33), "invalid port 33");

        BlackBox example = new BlackBox(
                Location.locationFromRowAndColumn(4, 5),
                Location.locationFromRowAndColumn(6, 6),
                Location.locationFromRowAndColumn(7, 4),
                Location.locationFromRowAndColumn(7, 5));
        checkEquals(24, trace(example, 1), "example detour");
        checkEquals(0, trace(example, 6), "example absorption");
        checkEquals(18, trace(example, 18), "example reflection");
        checkEquals(1, trace(example, 24), "example reverse detour");

        Laser reusable = new Laser(EdgeLocation.getEdgeLocationWithId(1));
        checkEquals(reusable.findExitEdgeLocation(example), reusable.findExitEdgeLocation(example),
                "tracing the same laser twice");

        // Reversibility of every non-absorbed shot for the first 5,000 configurations.
        Iterator<BlackBox> boards = BlackBoxStream.streamBlackBoxes().limit(5_000).iterator();
        int examined = 0;
        while (boards.hasNext()) {
            BlackBox board = boards.next();
            for (int id = 1; id <= 32; id++) {
                int exitId = trace(board, id);
                if (exitId != 0) {
                    checkEquals(id, trace(board, exitId), "reciprocity on board " + examined);
                }
            }
            examined++;
        }
        System.out.println("PASS: " + assertions + " checks; " + examined + " boards tested.");
    }

    private static int trace(BlackBox board, int port) {
        EdgeLocation exit = new Laser(EdgeLocation.getEdgeLocationWithId(port)).findExitEdgeLocation(board);
        return exit == null ? 0 : exit.getEdgeLocationId();
    }

    private static void checkEquals(Object expected, Object actual, String label) {
        assertions++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }

    private static void expectIllegalCoordinate(String value) {
        assertions++;
        try {
            Location.locationFromString(value);
            throw new AssertionError("Invalid coordinate was accepted: " + value);
        } catch (IllegalArgumentException expected) {
            // Expected: invalid coordinate is rejected rather than remapped.
        }
    }
}

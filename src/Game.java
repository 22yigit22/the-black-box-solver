import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Console assistant for identifying the four hidden balls from observed rays. */
public class Game {

    public static void main(String[] args) {
        List<BlackBox> boxes = BlackBoxStream.streamBlackBoxes().toList();
        System.out.println("Initial number of possible black boxes: " + boxes.size());

        Set<Integer> allEdgeIds = IntStream.rangeClosed(1, 32)
                .boxed().collect(Collectors.toCollection(TreeSet::new));
        // null means an absorbed ray; a non-null value is the exit edge ID.
        Map<Integer, Integer> knownResults = new HashMap<>();
        Random random = new Random();
        boolean firstShot = true;

        try (Scanner scanner = new Scanner(System.in)) {
            while (boxes.size() > 1) {
                List<Integer> unusedEdgeIds = allEdgeIds.stream()
                        .filter(id -> !knownResults.containsKey(id)).toList();
                if (unusedEdgeIds.isEmpty()) {
                    break;
                }

                // Before observing any result, every edge is symmetric.
                int entryId = firstShot
                        ? unusedEdgeIds.get(random.nextInt(unusedEdgeIds.size()))
                        : findBestEntryId(boxes, unusedEdgeIds);
                firstShot = false;

                System.out.printf("%nLaser fired from edge ID %d.%n", entryId);
                Observation observation = readObservation(scanner, entryId, knownResults);
                if (observation == null) {
                    System.out.println("Input ended; solver stopped without changing the candidates.");
                    return;
                }

                EdgeLocation entry = EdgeLocation.getEdgeLocationWithId(entryId);
                EdgeLocation exit = observation.exit();
                knownResults.put(entryId, exit == null ? null : exit.getEdgeLocationId());
                if (exit != null) {
                    knownResults.put(exit.getEdgeLocationId(), entryId);
                }

                boxes = boxes.stream()
                        .filter(box -> box.laserEntryExitMatches(entry, exit)).toList();
                System.out.println("Remaining possible black boxes: " + boxes.size());
            }
        }

        if (boxes.size() == 1) {
            System.out.println("\nSolved! The unique compatible board is:");
            System.out.println(boxes.get(0));
        } else if (boxes.isEmpty()) {
            System.out.println("\nNo boards match these observations. Check your inputs.");
        } else {
            System.out.println("\nAmbiguous: " + boxes.size() + " boards are compatible with all available rays.");
            System.out.println("Some boards have identical ray signatures and cannot be distinguished by probing.");
        }
    }

    /**
     * Returns null when the input stream is closed. Within an Observation,
     * a null exit means a valid H (absorbed) result.
     */
    private static Observation readObservation(Scanner scanner, int entryId,
                                               Map<Integer, Integer> knownResults) {
        while (true) {
            System.out.print("Enter result (exit ID 1-32, same ID if reflected, H if absorbed): ");
            if (!scanner.hasNext()) {
                return null;
            }
            String input = scanner.next();
            if (input.equalsIgnoreCase("H")) {
                return new Observation(null);
            }

            final int exitId;
            try {
                exitId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter H or a number between 1 and 32.");
                continue;
            }
            EdgeLocation exit = EdgeLocation.getEdgeLocationWithId(exitId);
            if (exit == null) {
                System.out.println("Edge ID must be between 1 and 32.");
                continue;
            }
            if (knownResults.containsKey(exitId)
                    && !Objects.equals(knownResults.get(exitId), entryId)) {
                System.out.println("That exit contradicts an earlier observation. Try again.");
                continue;
            }
            return new Observation(exit);
        }
    }

    private static int findBestEntryId(List<BlackBox> boxes, List<Integer> unusedEdgeIds) {
        int bestEntryId = -1;
        double bestEntropy = Double.NEGATIVE_INFINITY;
        for (int entryId : unusedEdgeIds) {
            EdgeLocation entry = EdgeLocation.getEdgeLocationWithId(entryId);
            int[] counts = new int[33]; // 0 = absorbed; 1..32 = exit edge.
            for (BlackBox box : boxes) {
                EdgeLocation exit = new Laser(entry).findExitEdgeLocation(box);
                counts[exit == null ? 0 : exit.getEdgeLocationId()]++;
            }
            double entropy = calculateEntropy(counts, boxes.size());
            if (entropy > bestEntropy) {
                bestEntropy = entropy;
                bestEntryId = entryId;
            }
        }
        return bestEntryId;
    }

    /** Shannon entropy, in bits, of the predicted ray outcomes. */
    private static double calculateEntropy(int[] counts, int total) {
        double entropy = 0.0;
        for (int count : counts) {
            if (count == 0) {
                continue;
            }
            double probability = (double) count / total;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
    }

    private record Observation(EdgeLocation exit) { }
}

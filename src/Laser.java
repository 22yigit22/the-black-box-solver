import java.util.Objects;

/** Simulates a ray entering an 8x8 Black Box board. */
public class Laser {
    private final EdgeLocation firedFrom;

    public Laser(EdgeLocation firedFrom) {
        this.firedFrom = Objects.requireNonNull(firedFrom, "Entry edge must not be null");
    }

    /**
     * @return the exit edge, or null when the ray hits a ball and is absorbed.
     * This method is repeatable: each invocation starts at the original entry.
     */
    public EdgeLocation findExitEdgeLocation(BlackBox blackBox) {
        Objects.requireNonNull(blackBox, "Black box must not be null");
        Location current = null;
        Location next = firedFrom.getAdjacentLocation();
        Direction direction = firedFrom.getDirectionToAdjacentLocation();

        while (true) {
            if (next == null) {
                return EdgeLocation.getEdgeLocationAdjacentTo(current, direction);
            }

            boolean ballDeadAhead = blackBox.ballExistsInLocation(next);
            boolean ballToTheRightAhead = blackBox.ballExistsInLocation(next.locationTowards(direction.right()));
            boolean ballToTheLeftAhead = blackBox.ballExistsInLocation(next.locationTowards(direction.left()));

            // A direct hit takes precedence over any sideways deflection.
            if (ballDeadAhead) {
                return null;
            } else if (ballToTheLeftAhead && ballToTheRightAhead) {
                // A double deflection reverses the path to the entry port.
                return firedFrom;
            } else if (ballToTheRightAhead) {
                if (current == null) {
                    return firedFrom; // Deflected before entering the board.
                }
                direction = direction.left();
                next = current.locationTowards(direction);
            } else if (ballToTheLeftAhead) {
                if (current == null) {
                    return firedFrom;
                }
                direction = direction.right();
                next = current.locationTowards(direction);
            } else {
                current = next;
                next = next.locationTowards(direction);
            }
        }
    }
}

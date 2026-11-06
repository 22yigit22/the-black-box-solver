package tr.edu.ozyegin.cs101.blackbox;

import java.util.Arrays;
import java.util.Objects;

public class EdgeLocation {
    private final int edgeLocationId;
    private final Location adjacentLocation;
    private final Direction directionToAdjacentLocation;

    private static final EdgeLocation[] edgeLocations = new EdgeLocation[32];

    static {
        for (int i = 0; i < 8; i++) {
            edgeLocations[i] = new EdgeLocation(i + 1, Location.locationFromRowAndColumn(0, i), Direction.S);
        }

        for (int i = 0; i < 8; i++) {
            edgeLocations[i + 8] = new EdgeLocation(i + 9, Location.locationFromRowAndColumn(i, 7), Direction.W);
        }

        for (int i = 0; i < 8; i++) {
            edgeLocations[i + 16] = new EdgeLocation(i + 17, Location.locationFromRowAndColumn(7, 7 - i), Direction.N);
        }

        for (int i = 0; i < 8; i++) {
            edgeLocations[i + 24] = new EdgeLocation(i + 25, Location.locationFromRowAndColumn(7 - i, 0), Direction.E);
        }
    }

    private EdgeLocation(int edgeLocationId, Location adjacentLocation, Direction directionToAdjacentLocation) {
        this.edgeLocationId = edgeLocationId;
        this.adjacentLocation = adjacentLocation;
        this.directionToAdjacentLocation = directionToAdjacentLocation;
    }

    public int getEdgeLocationId() {
        return edgeLocationId;
    }

    public Location getAdjacentLocation() {
        return adjacentLocation;
    }

    public Direction getDirectionToAdjacentLocation() {
        return directionToAdjacentLocation;
    }

    public static EdgeLocation getEdgeLocationWithId(int edgeLocationId) {
        return edgeLocationId >= 1 && edgeLocationId <= 32 ? edgeLocations[edgeLocationId - 1] : null;
    }

    public static EdgeLocation getEdgeLocationAdjacentTo(Location location, Direction direction) {
        return Arrays.stream(edgeLocations).filter(el -> el.adjacentLocation.equals(location))
                .filter(el -> el.directionToAdjacentLocation == direction.back()).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EdgeLocation other = (EdgeLocation) o;
        return this.edgeLocationId == other.edgeLocationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeLocationId);
    }
}

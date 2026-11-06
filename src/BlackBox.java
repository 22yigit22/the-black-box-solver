/**
 * Class representing a black box in
 * "The Black Box Game".
 */
public class BlackBox {
    private final Location[] ballLocations;


    public BlackBox(Location ballOne, Location ballTwo, Location ballThree, Location ballFour) {
        this.ballLocations = new Location[]{ballOne, ballTwo, ballThree, ballFour};
    }


    /**
     * Check for the existence of a ball in given location.
     *
     * @param location location to be checked.
     * @return true if ball exists in that location, false otherwise.
     */
    public boolean ballExistsInLocation(Location location) {
        return ballLocations[0].equals(location) || ballLocations[1].equals(location)
                || ballLocations[2].equals(location) || ballLocations[3].equals(location);
    }

    public boolean laserEntryExitMatches(EdgeLocation entry, EdgeLocation exit) {
        Laser laser = new Laser(entry);

        EdgeLocation actualExit = laser.findExitEdgeLocation(this);

        if (exit == null) {
            return actualExit == null;
        }

        return exit.equals(actualExit);
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < 64; i++) {
            if (this.ballExistsInLocation(Location.locationFromLinearPosition(i))) {
                buf.append("*");
            } else {
                buf.append(".");
            }

            if (i % 8 == 7) {
                buf.append("\n");
            }
        }

        return buf.toString();

    }

    public static void main(String[] args) {
        // Test black box

        BlackBox blackBox = new BlackBox(Location.locationFromRowAndColumn(4, 5),
                                         Location.locationFromRowAndColumn(6, 6),
                                         Location.locationFromRowAndColumn(7, 4),
                                         Location.locationFromRowAndColumn(7, 5));


        for (int i = 1; i <= 32; i++) {
            Laser laser = new Laser(EdgeLocation.getEdgeLocationWithId(i));

            EdgeLocation exitEdgeLocation = laser.findExitEdgeLocation(blackBox);

            System.out.println("Enter: " + i + " exit: " + (exitEdgeLocation == null ? "H" : exitEdgeLocation.getEdgeLocationId()));

        }



    }


}

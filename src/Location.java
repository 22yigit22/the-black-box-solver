import java.util.Objects;

/**
 * Class representing a location.
 *
 * The "external" representation is as follows:
 *
 * A1 A2 A3 A4 A5 A6 A7 A8
 * B1 B2 B3 B4 B5 B6 B7 B8
 * C1 C2 C3 C4 C5 C6 C7 C8
 * D1 D2 D3 D4 D5 D6 D7 D8
 * E1 E2 E3 E4 E5 E6 E7 E8
 * F1 F2 F3 F4 F5 F6 F7 F8
 * G1 G2 G3 G4 G5 G6 G7 G8
 * H1 H2 H3 H4 H5 H6 H7 H8
 *
 * This is for getting external input, and displaying
 * output to the user. However, internally things are
 * different. The internal representation is:
 *
 *  0  1  2  3  4  5  6  7
 *  8  9 10 11 12 13 14 15
 * 16 17 18 19 20 21 22 23
 * 24 25 26 27 28 29 30 31
 * 32 33 34 35 36 37 38 39
 * 40 41 42 43 44 45 46 47
 * 48 49 50 51 52 53 54 55
 * 56 57 58 59 60 61 62 63
 *
 * This is useful, since it makes computing row and
 * column numbers much easier. If the position number
 * is just n, then the column number (x) and row number
 * (y) relations are simple:
 *
 * y = n / 8   (integer division, range is 0..7)
 * x = n % 8   (modulus, range is 0..7)
 *
 * The inverse computation is also simple:
 *
 * n = 8*y + x;
 * */

public class Location {

    private final static int MAX = 63;

    private final int n; // Internally we only keep n

    /**
     * Private constructor. Can only be invoked from factory methods in this class.
     * @param n the internal representation.
     */
    private Location(int n) {
        this.n = n;
    }

    public static Location locationFromLinearPosition(int n) {
        if (n < 0 || n > MAX) {
            return null;
        }
        return new Location(n);
    }

    /** Parse a board coordinate such as A1 or H8. */
    public static Location locationFromString(String location) {
        if (location == null || location.length() != 2
                || location.charAt(0) < 'A' || location.charAt(0) > 'H'
                || location.charAt(1) < '1' || location.charAt(1) > '8') {
            throw new IllegalArgumentException("Expected a coordinate from A1 to H8.");
        }

        int row = location.charAt(0) - 'A';
        int column = location.charAt(1) - '1';
        return locationFromRowAndColumn(row, column);
    }

    public static Location locationFromRowAndColumn(int row, int column) {
        if (row < 0 || row > 7 || column < 0 || column > 7) {
            return null;
        }

        return locationFromLinearPosition(8*row + column);
    }

    public int getRow() {
        return this.n / 8;
    }

    public int getColumn() {
        return this.n % 8;
    }

    public int getN() {
        return this.n;
    }

    @Override
    public String toString() {
        return "" + (char)(this.getRow() + 'A') + (char)(this.getColumn() + '1');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location other = (Location) o;
        return this.n == other.n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n);
    }

    public static void main(String[] args) {
        Location l = locationFromString("A3");

        System.out.println(l);
    }

    public Location locationTowards(Direction direction) {
        return locationFromRowAndColumn(this.getRow() + direction.getDeltaRow(), this.getColumn() + direction.getDeltaColumn());
    }

}

public enum Direction {
    N(-1, 0), E(0, 1), S(1, 0), W(0, -1);
    private final int deltaRow;
    private final int deltaColumn;

    Direction(int deltaRow, int deltaColumn) {
        this.deltaRow = deltaRow;
        this.deltaColumn = deltaColumn;
    }

    public int getDeltaRow() {
        return this.deltaRow;
    }

    public int getDeltaColumn() {
        return this.deltaColumn;
    }

    public Direction left() {
        return switch (this) {
            case N -> W;
            case E -> N;
            case S -> E;
            case W -> S;
        };
    }

    public Direction right() {
        return switch (this) {
            case N -> E;
            case E -> S;
            case S -> W;
            case W -> N;

        };
    }

    public Direction back() {
        return switch (this) {
            case N -> S;
            case E -> W;
            case S -> N;
            case W -> E;
        };
    }


}

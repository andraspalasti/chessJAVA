package chess.core;

public class Square {
    private static final Square[] cornerSquares = new Square[] {
            new Square(0, 0), // Top left corner
            new Square(0, Board.WIDTH - 1), // Top right corner
            new Square(Board.HEIGHT - 1, 0), // Bottom left corner
            new Square(Board.HEIGHT - 1, Board.WIDTH - 1) // Bottom right corner
    };

    /**
     * The row on a chess board
     */
    public final int rank;

    /**
     * The column on a chess board
     */
    public final int file;

    public Square(int rank, int file) {
        this.rank = rank;
        this.file = file;
    }

    public Square(String square) {
        square = square.trim();
        if (square.length() != 2) {
            throw new IllegalArgumentException("Illegal square: " + square);
        }

        int rank = Board.HEIGHT - (square.charAt(1) - '0');
        int file = Character.toLowerCase(square.charAt(0)) - 'a';
        if (rank < 0 || Board.HEIGHT <= rank) {
            throw new IllegalArgumentException("Illegal rank in square: " + square.charAt(1));
        }
        if (file < 0 || Board.WIDTH <= file) {
            throw new IllegalArgumentException("Illegal file in square: " + square.charAt(0));
        }
        this.rank = rank;
        this.file = file;
    }

    public int getRow() {
        return rank;
    }

    public int getCol() {
        return file;
    }

    public boolean isCorner() {
        for (Square corner : cornerSquares) {
            if (this.equals(corner)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTopLeft() {
        return this.equals(cornerSquares[0]);
    }

    public boolean isTopRight() {
        return this.equals(cornerSquares[1]);
    }

    public boolean isBottomLeft() {
        return this.equals(cornerSquares[2]);
    }

    public boolean isBottomRight() {
        return this.equals(cornerSquares[3]);
    }

    public boolean isWhite() {
        return (rank + file) % 2 == 0;
    }

    @Override
    public boolean equals(Object obj) {
        Square other = (Square) obj;
        return this.rank == other.rank && this.file == other.file;
    }

    @Override
    public String toString() {
        return String.format("%c%c", (char) this.file + 'a', (char) Board.HEIGHT - this.rank + '0');
    }
}

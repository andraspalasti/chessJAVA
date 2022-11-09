package chess.core;

public class Square {
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

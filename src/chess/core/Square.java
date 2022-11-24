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

    /**
     * Returns the row which the square is positioned in.
     * 
     * @return The row of the square
     */
    public int getRow() {
        return rank;
    }

    /**
     * Returns the column which the square is positioned in.
     * 
     * @return The column of the square
     */
    public int getCol() {
        return file;
    }

    /**
     * Checks if the square is one of the four corners.
     * 
     * @return True if the square is a corner square
     */
    public boolean isCorner() {
        for (Square corner : cornerSquares) {
            if (this.equals(corner)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the square is in the top left corner.
     * 
     * @return True if the square is in the top left corner else false
     */
    public boolean isTopLeft() {
        return this.equals(cornerSquares[0]);
    }

    /**
     * Checks if the square is in the top right corner.
     * 
     * @return True if the square is in the top right corner else false
     */
    public boolean isTopRight() {
        return this.equals(cornerSquares[1]);
    }

    /**
     * Checks if the square is in the bottom left corner.
     * 
     * @return True if the square is in the bottom left corner else false
     */
    public boolean isBottomLeft() {
        return this.equals(cornerSquares[2]);
    }

    /**
     * Checks if the square is in the bottom right corner.
     * 
     * @return True if the square is in the bottom right corner else false
     */
    public boolean isBottomRight() {
        return this.equals(cornerSquares[3]);
    }

    /**
     * Checks the color of the square.
     * 
     * @return If the square is white it returns true else false
     */
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

package chess.core;

public enum PieceColor {
    WHITE, BLACK;

    /**
     * Returns the opposite color of the current one.
     * 
     * @return The opposite color of the current one.
     */
    public PieceColor getInverse() {
        return this == WHITE ? BLACK : WHITE;
    }
}
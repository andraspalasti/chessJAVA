package chess.core;

public enum PieceColor {
    WHITE, BLACK;

    public PieceColor getInverse() {
        return this == WHITE ? BLACK : WHITE;
    }
}
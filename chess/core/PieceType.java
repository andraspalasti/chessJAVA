package chess.core;

public enum PieceType {
    King(Integer.MAX_VALUE),
    Queen(8),
    Rook(5),
    Bishop(3),
    Knight(3),
    Pawn(1);

    private final int value;

    private PieceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

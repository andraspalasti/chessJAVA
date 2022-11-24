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

    /**
     * Returns the value of the piece.
     * 
     * @return The value of this specific piece type.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns a piece type from a character according to FEN notation.
     * 
     * @param c The character which the piece type is encoded in
     * @return The piece type
     */
    public static PieceType fromCharacter(char c) {
        switch (Character.toUpperCase(c)) {
            case 'K':
                return King;
            case 'Q':
                return Queen;
            case 'R':
                return Rook;
            case 'B':
                return Bishop;
            case 'N':
                return Knight;
            case 'P':
                return Pawn;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case King:
                return "K";
            case Queen:
                return "Q";
            case Rook:
                return "R";
            case Bishop:
                return "B";
            case Knight:
                return "N";
            default:
                return "";
        }
    }
}

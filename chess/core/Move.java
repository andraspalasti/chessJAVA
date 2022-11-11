package chess.core;

public class Move {
    public final Square from, to;
    private Piece capturedPiece = null;
    private byte prevCastlingRights;

    public Move(Square from, Square to) {
        this.from = from;
        this.to = to;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    protected void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public byte getPrevCastlingRights() {
        return prevCastlingRights;
    }

    public void setPrevCastlingRights(byte prevCastlingRights) {
        this.prevCastlingRights = prevCastlingRights;
    }

    public boolean isKingSideCastle() {
        return from.file == 4 && to.file == 6;
    }

    public boolean isQueenSideCastle() {
        return from.file == 4 && to.file == 2;
    }

    @Override
    public boolean equals(Object obj) {
        Move other = (Move) obj;
        return this.to.equals(other.to) && this.from.equals(other.from);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}

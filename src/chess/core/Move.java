package chess.core;

public class Move {
    public final Square from, to;
    private Piece movedPiece, capturedPiece = null;
    private PieceType promotionTo = PieceType.Queen;
    private byte prevCastlingRights;

    public Move(Square from, Square to) {
        this.from = from;
        this.to = to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    protected void setMovedPiece(Piece movedPiece) {
        this.movedPiece = movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    protected void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    protected byte getPrevCastlingRights() {
        return prevCastlingRights;
    }

    protected void setPrevCastlingRights(byte prevCastlingRights) {
        this.prevCastlingRights = prevCastlingRights;
    }

    public PieceType getPromotionTo() {
        return promotionTo;
    }

    public void setPromotionTo(PieceType promoteTo) {
        this.promotionTo = promoteTo;
    }

    protected boolean isKingsideCastle() {
        return from.file == 4 && to.file == 6;
    }

    protected boolean isQueensideCastle() {
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

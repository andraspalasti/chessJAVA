package chess.core;

public class Move {
    public final Square from, to;
    private Piece movedPiece, capturedPiece = null;
    private PieceType promotionTo = PieceType.Queen;
    private byte prevCastlingRights;

    protected Move(Square from, Square to) {
        this.from = from;
        this.to = to;
    }

    public boolean isKingsideCastle() {
        if (movedPiece == null) {
            return false;
        }
        return movedPiece.getType() == PieceType.King && from.file == 4 && to.file == 6;
    }

    public boolean isQueensideCastle() {
        if (movedPiece == null) {
            return false;
        }
        return movedPiece.getType() == PieceType.King && from.file == 4 && to.file == 2;
    }

    public boolean isPromotion() {
        if (movedPiece == null) {
            return false;
        }
        int promotionRow = movedPiece.getColor() == PieceColor.WHITE ? 0 : Board.HEIGHT - 1;
        return movedPiece.getType() == PieceType.Pawn && to.rank == promotionRow;
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

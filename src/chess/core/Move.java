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

    /**
     * Checks if the move is a kingside castle.
     * 
     * @return True if the move is a kingside castle else false
     */
    protected boolean isKingsideCastle() {
        if (movedPiece == null) {
            return false;
        }
        return movedPiece.getType() == PieceType.King && from.file == 4 && to.file == 6;
    }

    /**
     * Checks if the move is a queenside castle.
     * 
     * @return True if the move is a queenside castle else false
     */
    protected boolean isQueensideCastle() {
        if (movedPiece == null) {
            return false;
        }
        return movedPiece.getType() == PieceType.King && from.file == 4 && to.file == 2;
    }

    /**
     * Checks if this is a pawn move to the promotion row.
     * 
     * @return True if you can promote the pawn after the move else false
     */
    protected boolean isPromotion() {
        if (movedPiece == null) {
            return false;
        }
        int promotionRow = movedPiece.getColor() == PieceColor.WHITE ? 0 : Board.HEIGHT - 1;
        return movedPiece.getType() == PieceType.Pawn && to.rank == promotionRow;
    }

    /**
     * Returns the piece which the move moved.
     * 
     * @return The piece which is moved
     */
    protected Piece getMovedPiece() {
        return movedPiece;
    }

    /**
     * Sets the moved piece for the move.
     * 
     * @param movedPiece The moved piece
     */
    protected void setMovedPiece(Piece movedPiece) {
        this.movedPiece = movedPiece;
    }

    /**
     * Returns the captured piece.
     * 
     * @return The captured piece
     */
    protected Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Sets the captured piece.
     * 
     * @param capturedPiece The captured piece
     */
    protected void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    /**
     * This method is used for undoing, to get the castling rights prior to this
     * move.
     * 
     * @return Castling rights prior to this move
     */
    protected byte getPrevCastlingRights() {
        return prevCastlingRights;
    }

    /**
     * Sets the castling rights.
     * 
     * @param prevCastlingRights The castling rights
     */
    protected void setPrevCastlingRights(byte prevCastlingRights) {
        this.prevCastlingRights = prevCastlingRights;
    }

    /**
     * Returns the type of the piece that we are promoting to after a promoting move
     * has been played.
     * 
     * @return The type of the piece to which we promote to
     */
    public PieceType getPromotionTo() {
        return promotionTo;
    }

    /**
     * Sets the type of the piece that we are promoting to.
     * 
     * @param promoteTo The type of the piece that we are promoting to 
     */
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

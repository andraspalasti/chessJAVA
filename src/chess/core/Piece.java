package chess.core;

import java.util.List;

public abstract class Piece {
    protected Board board;
    protected PieceColor color;

    /**
     * Constructor for the piece, you have to provide the board that the piece is
     * standing on and the color of the piece.
     * 
     * @param board The board on which the piece is standing on
     * @param color The color of the piece
     */
    public Piece(Board board, PieceColor color) {
        this.board = board;
        this.color = color;
    }

    /**
     * Returns a list of all of the available pseudo legal moves of the piece.
     * 
     * @return List of all of the pseudo legal moves
     */
    public abstract List<Move> generateMoves();

    /**
     * Returns the type of the piece.
     * 
     * @return The type of the piece
     */
    public abstract PieceType getType();

    /**
     * Returns the color of the piece.
     * 
     * @return The color of the piece
     */
    public PieceColor getColor() {
        return color;
    }

    /**
     * Checks if the specified move can be made for the piece
     * 
     * @param move The move to check
     * @return True if the move can be made else false
     */
    public boolean canMakeMove(Move move) {
        Square square = this.board.findPiece(this);
        if (!move.from.equals(square)) {
            return false;
        }
        List<Move> moves = this.generateMoves();
        return moves.contains(move);
    }

    /**
     * Checks if the piece is attacking the specified square (attacking = able to
     * move there)
     * 
     * @param square The square to check
     * @return True if the piece is attacking the square else false
     */
    public boolean isAttacking(Square square) {
        for (Move possibleMove : this.generateMoves()) {
            if (possibleMove.to.equals(square)) {
                return true;
            }
        }
        return false;
    }
}

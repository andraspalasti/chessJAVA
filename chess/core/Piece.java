package chess.core;

import java.util.List;

public abstract class Piece {
    protected Board board;
    protected PieceColor color;

    public Piece(Board board, PieceColor color) {
        this.board = board;
        this.color = color;
    }

    public abstract List<Move> generateMoves();

    public abstract PieceType getType();

    public boolean canMakeMove(Move move) {
        Square square = this.board.findPiece(this);
        if (!move.from.equals(square)) {
            return false;
        }
        List<Move> moves = this.generateMoves();
        return moves.contains(move);
    }

    public PieceColor getColor() {
        return color;
    }
}

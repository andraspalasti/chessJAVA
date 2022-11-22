package chess.core;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Board board, PieceColor color) {
        super(board, color);
    }

    @Override
    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<Move>();
        // check if the piece is the color to move
        if (board.activeColor != color) {
            return moves;
        }

        Square origin = board.findPiece(this);
        // check if the piece is on the board
        if (origin == null) {
            return moves;
        }

        int forwardDir = this.color == PieceColor.WHITE ? -1 : 1;

        // Check for pawn push
        Square push = new Square(origin.rank + forwardDir, origin.file);
        if (board.isLegalSquare(push) && board.getPiece(push) == null) {
            moves.add(new Move(origin, push));

            // Check for double pawn push
            int initalRank = this.color == PieceColor.WHITE ? Board.HEIGHT - 2 : 1;
            Square doublePush = new Square(origin.rank + 2 * forwardDir, origin.file);
            if (initalRank == origin.rank && board.getPiece(doublePush) == null) {
                moves.add(new Move(origin, doublePush));
            }
        }

        // Check for pawn captures
        Square leftCapture = new Square(origin.rank + forwardDir, push.file + 1),
                rightCapture = new Square(origin.rank + forwardDir, push.file - 1);
        if (containsOpponentPiece(leftCapture)) {
            moves.add(new Move(origin, leftCapture));
        }
        if (containsOpponentPiece(rightCapture)) {
            moves.add(new Move(origin, rightCapture));
        }
        return moves;
    }

    public boolean containsOpponentPiece(Square square) {
        if (board.isLegalSquare(square)) {
            Piece p = board.getPiece(square);
            if (p != null && p.getColor() != color) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PieceType getType() {
        return PieceType.Pawn;
    }
}

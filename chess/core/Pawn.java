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

        // TODO: Implement en passant move and check
        // TODO: Implement pawn captures

        // Check pawn push
        int forwardDir = this.color == PieceColor.WHITE ? -1 : 1;
        Square dest = new Square(origin.rank + forwardDir, origin.file);
        if (this.board.isLegalSquare(dest) && this.board.getPiece(dest) == null) {
            moves.add(new Move(origin, dest));
        } else {
            return moves;
        }

        // Check double pawn push
        int initalRank = this.color == PieceColor.WHITE ? Board.HEIGHT - 2 : 1;
        dest = new Square(origin.rank + 2 * forwardDir, origin.file);
        if (origin.rank == initalRank && this.board.getPiece(dest) == null) {
            moves.add(new Move(origin, dest));
        }
        return moves;
    }

    @Override
    public PieceType getType() {
        return PieceType.Pawn;
    }
}

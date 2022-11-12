package chess.core;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    private static final int[][] moveDirections = new int[][] {
            { -1, 1 }, { -1, -1 }, { 1, -1 }, { 1, 1 }
    };

    public Bishop(Board board, PieceColor color) {
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

        for (int[] dir : moveDirections) {
            int rank = origin.rank + dir[0], file = origin.file + dir[1];
            while (board.isLegalSquare(file, rank)) {
                Piece p = board.squares[rank][file];
                if (p == null) {
                    moves.add(new Move(origin, new Square(rank, file)));
                } else {
                    if (p.getColor() != color) {
                        moves.add(new Move(origin, new Square(rank, file)));
                    }
                    break;
                }
                rank += dir[0];
                file += dir[1];
            }
        }

        return moves;
    }

    @Override
    public PieceType getType() {
        return PieceType.Bishop;
    }
}

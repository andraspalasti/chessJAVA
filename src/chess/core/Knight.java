package chess.core;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    // The offsets that the piece can move in: {rank, file}
    private static final int[][] moveDirections = new int[][] {
            { 2, 1 }, { 2, -1 }, { 1, 2 }, { 1, -2 },
            { -2, 1 }, { -2, -1 }, { -1, 2 }, { -1, -2 }
    };

    public Knight(Board board, PieceColor color) {
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
            Square dest = new Square(origin.rank + dir[0], origin.file + dir[1]);
            if (board.isLegalSquare(dest)) {
                Piece p = board.squares[dest.rank][dest.file];
                if (p == null || p.getColor() != color) {
                    moves.add(new Move(origin, dest));
                }
            }
        }

        return moves;
    }

    @Override
    public PieceType getType() {
        return PieceType.Knight;
    }
}

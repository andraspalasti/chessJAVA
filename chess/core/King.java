package chess.core;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private static final int[][] moveDirections = new int[][] {
            { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 },
            { -1, 1 }, { -1, -1 }, { 1, -1 }, { 1, 1 }
    };

    public King(Board board, PieceColor color) {
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

        // TODO: don't allow castling if an opponent piece is attacking one of the
        // castling squares

        // Handle castling
        int rank = color == PieceColor.WHITE ? Board.HEIGHT - 1 : 0;
        if (canKingSideCastle()) {
            moves.add(new Move(origin, new Square(rank, 6)));
        }
        if (canQueenSideCastle()) {
            moves.add(new Move(origin, new Square(rank, 2)));
        }

        return moves;
    }

    private boolean canKingSideCastle() {
        boolean hasRight = color == PieceColor.WHITE ? board.whiteCastleKingside : board.blackCastleKingside;
        int rank = color == PieceColor.WHITE ? Board.HEIGHT - 1 : 0;
        return hasRight
                && board.squares[rank][5] == null
                && board.squares[rank][6] == null;
    }

    private boolean canQueenSideCastle() {
        boolean hasRight = color == PieceColor.WHITE ? board.whiteCastleQueenside : board.blackCastleQueenside;
        int rank = color == PieceColor.WHITE ? Board.HEIGHT - 1 : 0;
        return hasRight
                && board.squares[rank][1] == null
                && board.squares[rank][2] == null
                && board.squares[rank][3] == null;
    }

    @Override
    public PieceType getType() {
        return PieceType.King;
    }
}

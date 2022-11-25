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

        // Handle castling
        int rank = color == PieceColor.WHITE ? Board.HEIGHT - 1 : 0;
        if (canCastleKingside()) {
            moves.add(new Move(origin, new Square(rank, 6)));
        }
        if (canCastleQueenside()) {
            moves.add(new Move(origin, new Square(rank, 2)));
        }

        return moves;
    }

    private boolean canCastleKingside() {
        // Check the castling rights
        if (!board.canCastleKingside(color)) {
            return false;
        }

        // Check if there are piecies in the way
        int rank = color == PieceColor.WHITE ? Board.HEIGHT - 1 : 0;
        if (board.squares[rank][5] != null || board.squares[rank][6] != null) {
            return false;
        }

        // Check that no square is attacked where the king moves
        for (int i = 0; i < Board.WIDTH * Board.HEIGHT; i++) {
            Piece piece = board.squares[i / Board.WIDTH][i % Board.WIDTH];
            if (piece != null && piece.getColor() != color) {
                // Enemy piece
                boolean isAttacking = piece.isAttacking(new Square(rank, 4)) || piece.isAttacking(new Square(rank, 5))
                        || piece.isAttacking(new Square(rank, 6));
                if (isAttacking) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canCastleQueenside() {
        // Check the castling rights
        if (!board.canCastleQueenside(color)) {
            return false;
        }

        // Check if there are piecies in the way
        int rank = color == PieceColor.WHITE ? Board.HEIGHT - 1 : 0;
        if (board.squares[rank][1] != null || board.squares[rank][2] != null || board.squares[rank][3] != null) {
            return false;
        }

        // Check that no square is attacked where the king moves
        for (int i = 0; i < Board.WIDTH * Board.HEIGHT; i++) {
            Piece piece = board.squares[i / Board.WIDTH][i % Board.WIDTH];
            if (piece != null && piece.getColor() != color) {
                // Enemy piece
                boolean isAttacking = piece.isAttacking(new Square(rank, 2)) || piece.isAttacking(new Square(rank, 3))
                        || piece.isAttacking(new Square(rank, 4));
                if (isAttacking) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public PieceType getType() {
        return PieceType.King;
    }
}

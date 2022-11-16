package chess.core;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int WIDTH = 8, HEIGHT = 8;

    private static final String STARTING_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final byte whiteKingsideMask = (byte) 0b0001,
            whiteQueensideMask = (byte) 0b0010,
            blackKingsideMask = (byte) 0b0100,
            blackQueensideMask = (byte) 0b1000;

    /**
     * A byte representing the castling rights of the board.
     */
    private byte castlingRights;

    protected Piece[][] squares;
    protected PieceColor activeColor;
    protected Square enPassantSquare;

    protected List<Move> moveHistory;

    private void initalize() {
        this.squares = new Piece[HEIGHT][WIDTH];
        this.activeColor = PieceColor.WHITE;
        this.castlingRights = 0b1111;
        this.enPassantSquare = null;

        this.moveHistory = new ArrayList<>();
    }

    public Board() {
        initalize();
        try {
            FenParser.loadFen(this, STARTING_POS);
        } catch (Exception e) {
        }
    }

    public Piece getPiece(Square square) {
        return getPiece(square.rank, square.file);
    }

    public Piece getPiece(int row, int col) {
        if (row < 0 || HEIGHT <= row)
            throw new IllegalArgumentException("Row out of bounds");

        if (col < 0 || WIDTH <= col)
            throw new IllegalArgumentException("Column out of bounds");

        return squares[row][col];
    }

    public int getMoveCount() {
        return moveHistory.size();
    }

    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < Board.WIDTH * Board.HEIGHT; i++) {
            Piece piece = squares[i / Board.WIDTH][i % Board.WIDTH];
            if (piece != null && piece.getColor() == activeColor) {
                // Filter out only pseudo legal moves
                for (Move move : piece.generateMoves()) {
                    if (isLegal(move)) {
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    public void makeMove(Move move) {
        if (!isLegalSquare(move.from)) {
            throw new IllegalArgumentException("The source square of the move is outside of the board");
        }
        if (!isLegalSquare(move.to)) {
            throw new IllegalArgumentException("The target square of the move is outside of the board");
        }

        Piece piece = getPiece(move.from);
        if (piece == null) {
            throw new IllegalArgumentException("There is no piece on the source square of the move");
        }
        if (!piece.canMakeMove(move)) {
            throw new IllegalArgumentException("Illegal move");
        }

        if (!isLegal(move)) {
            throw new IllegalArgumentException("Illegal move, this move is only pseudo legal");
        }
        mustMakeMove(move);
    }

    private boolean isLegal(Move move) {
        PieceColor curColor = activeColor;
        mustMakeMove(move);

        // Find position of the king
        Square kingPos = null;
        for (int i = 0; i < Board.WIDTH * Board.HEIGHT; i++) {
            Piece piece = squares[i / Board.WIDTH][i % Board.WIDTH];
            if (piece != null && piece.getColor() == curColor && piece.getType() == PieceType.King) {
                kingPos = new Square(i / Board.WIDTH, i % Board.WIDTH);
            }
        }

        // Check that anything is attacking it
        for (int i = 0; i < Board.WIDTH * Board.HEIGHT; i++) {
            Piece piece = squares[i / Board.WIDTH][i % Board.WIDTH];
            if (piece != null && piece.getColor() == activeColor && piece.isAttacking(kingPos)) {
                undoMove();
                return false;
            }
        }
        undoMove();
        return true;
    }

    private void mustMakeMove(Move move) {
        Square src = move.from, dest = move.to;
        Piece piece = getPiece(src);
        Piece capturedPiece = getPiece(dest);

        squares[src.getRow()][src.getCol()] = null;
        squares[dest.getRow()][dest.getCol()] = piece;
        move.setCapturedPiece(capturedPiece);
        move.setPrevCastlingRights(castlingRights);

        // handle castling
        if (piece.getType() == PieceType.King) {
            if (move.isKingSideCastle()) {
                squares[dest.getRow()][dest.getCol() - 1] = squares[src.getRow()][Board.WIDTH - 1];
                squares[src.getRow()][Board.WIDTH - 1] = null;
            }
            if (move.isQueenSideCastle()) {
                squares[dest.getRow()][dest.getCol() + 1] = squares[src.getRow()][0];
                squares[src.getRow()][0] = null;
            }

            // clear castling rights
            if (piece.getColor() == PieceColor.WHITE)
                castlingRights &= (~whiteKingsideMask & ~whiteQueensideMask);
            else
                castlingRights &= (~blackKingsideMask & ~blackQueensideMask);
        }

        // adjust castling rights
        if (piece.getType() == PieceType.Rook && src.isCorner()) {
            if (src.isTopLeft())
                castlingRights &= ~blackQueensideMask;
            else if (src.isTopRight())
                castlingRights &= ~blackKingsideMask;
            else if (src.isBottomLeft())
                castlingRights &= ~whiteQueensideMask;
            else if (src.isBottomRight())
                castlingRights &= ~whiteKingsideMask;
        }

        if (capturedPiece != null && capturedPiece.getType() == PieceType.Rook && dest.isCorner()) {
            if (dest.isTopLeft())
                castlingRights &= ~blackQueensideMask;
            else if (dest.isTopRight())
                castlingRights &= ~blackKingsideMask;
            else if (dest.isBottomLeft())
                castlingRights &= ~whiteQueensideMask;
            else if (dest.isBottomRight())
                castlingRights &= ~whiteKingsideMask;
        }

        // TODO: handle en pasant
        activeColor = activeColor.getInverse();
        moveHistory.add(move);
    }

    public void undoMove() {
        if (moveHistory.size() == 0) {
            return;
        }

        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        Square src = lastMove.from, dest = lastMove.to;
        Piece movedPiece = getPiece(dest);

        squares[dest.getRow()][dest.getCol()] = lastMove.getCapturedPiece();
        squares[src.getRow()][src.getCol()] = movedPiece;

        // handle castling undo
        if (movedPiece.getType() == PieceType.King) {
            if (lastMove.isKingSideCastle()) {
                squares[src.getRow()][Board.WIDTH - 1] = squares[dest.getRow()][dest.getCol() - 1];
                squares[dest.getRow()][dest.getCol() - 1] = null;
            }
            if (lastMove.isQueenSideCastle()) {
                squares[src.getRow()][0] = squares[dest.getRow()][dest.getCol() + 1];
                squares[dest.getRow()][dest.getCol() + 1] = null;
            }
        }

        castlingRights = lastMove.getPrevCastlingRights();
        activeColor = activeColor.getInverse();
        // TODO: handle en pasant
    }

    protected Square findPiece(Piece p) {
        for (int rank = 0; rank < squares.length; rank++) {
            for (int file = 0; file < squares[rank].length; file++) {
                if (squares[rank][file] == p) {
                    return new Square(rank, file);
                }
            }
        }
        return null;
    }

    public Move getLastMove() {
        if (0 < moveHistory.size()) {
            return moveHistory.get(moveHistory.size() - 1);
        }
        return null;
    }

    public boolean canCastleKingside(PieceColor color) {
        return (castlingRights & (color == PieceColor.WHITE ? whiteKingsideMask : blackKingsideMask)) != 0;
    }

    public boolean canCastleQueenside(PieceColor color) {
        return (castlingRights & (color == PieceColor.WHITE ? whiteQueensideMask : blackQueensideMask)) != 0;
    }

    public boolean isLegalSquare(int row, int col) {
        return (0 <= col && col < Board.HEIGHT)
                && (0 <= row && row < Board.WIDTH);
    }

    public boolean isLegalSquare(Square pos) {
        return isLegalSquare(pos.rank, pos.file);
    }
}

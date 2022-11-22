package chess.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import chess.core.PGNParser.InvalidPGNException;

public class Board {
    public static final int WIDTH = 8, HEIGHT = 8;

    private static final String STARTING_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

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

        int row = 0, col = 0;
        for (char c : STARTING_POS.toCharArray()) {
            if (c == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                col += (int) (c - '1');
            } else {
                PieceType type = PieceType.fromCharacter(c);
                PieceColor color = Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK;
                squares[row][col] = createPiece(type, color);
                col++;
            }
        }
    }

    public Board() {
        initalize();
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
                        move.setMovedPiece(piece);
                        move.setCapturedPiece(getPiece(move.to));
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
        if (move.isPromotion() && move.getPromotionTo() == null) {
            throw new IllegalArgumentException("No piece was given to promote to");
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

        // Set move metadate for undoing
        move.setMovedPiece(piece);
        move.setCapturedPiece(capturedPiece);
        move.setPrevCastlingRights(castlingRights);

        squares[src.getRow()][src.getCol()] = null;
        if (move.isPromotion()) {
            squares[dest.getRow()][dest.getCol()] = createPiece(move.getPromotionTo(), activeColor);
        } else {
            squares[dest.getRow()][dest.getCol()] = piece;
        }

        // handle castling
        if (piece.getType() == PieceType.King) {
            if (move.isKingsideCastle()) {
                squares[dest.getRow()][dest.getCol() - 1] = squares[src.getRow()][Board.WIDTH - 1];
                squares[src.getRow()][Board.WIDTH - 1] = null;
            }
            if (move.isQueensideCastle()) {
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
        Piece movedPiece = lastMove.getMovedPiece();

        squares[dest.getRow()][dest.getCol()] = lastMove.getCapturedPiece();
        squares[src.getRow()][src.getCol()] = movedPiece;

        // handle castling undo
        if (movedPiece.getType() == PieceType.King) {
            if (lastMove.isKingsideCastle()) {
                squares[src.getRow()][Board.WIDTH - 1] = squares[dest.getRow()][dest.getCol() - 1];
                squares[dest.getRow()][dest.getCol() - 1] = null;
            }
            if (lastMove.isQueensideCastle()) {
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

    public Move[] getMoves() {
        Move[] moves = new Move[moveHistory.size()];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = moveHistory.get(i);
        }
        return moves;
    }

    public PieceColor getActiveColor() {
        return activeColor;
    }

    public void loadPGN(String pgn) throws InvalidPGNException {
        initalize();
        PGNParser.loadPGN(this, pgn);
    }

    public void writeMoves(PrintWriter pw) {
        PGNParser.writePGN(pw, this);
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

    protected Piece createPiece(PieceType type, PieceColor color) {
        switch (type) {
            case King:
                return new King(this, color);
            case Queen:
                return new Queen(this, color);
            case Rook:
                return new Rook(this, color);
            case Bishop:
                return new Bishop(this, color);
            case Knight:
                return new Knight(this, color);
            case Pawn:
                return new Pawn(this, color);
            default:
                return null;
        }
    }
}

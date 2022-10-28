package chess.core;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int WIDTH = 8, HEIGHT = 8;

    private static final String STARTING_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // Board state
    protected Piece[][] squares;
    protected PieceColor activeColor;
    protected boolean whiteCastleKingside = false;
    protected boolean whiteCastleQueenside = false;
    protected boolean blackCastleKingside = false;
    protected boolean blackCastleQueenside = false;
    protected Square enPassantSquare;

    protected List<Move> moveHistory;

    public void initalize() {
        this.squares = new Piece[HEIGHT][WIDTH];
        this.activeColor = PieceColor.WHITE;
        this.whiteCastleKingside = false;
        this.whiteCastleQueenside = false;
        this.blackCastleKingside = false;
        this.blackCastleQueenside = false;
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

    public Piece getPiece(int rank, int file) {
        if (rank < 0 || HEIGHT <= rank)
            throw new IllegalArgumentException("Rank out of bounds");

        if (file < 0 || WIDTH <= file)
            throw new IllegalArgumentException("File out of bounds");

        return this.squares[rank][file];
    }

    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < Board.WIDTH * Board.HEIGHT; i++) {
            Piece piece = squares[i / Board.WIDTH][i % Board.WIDTH];
            if (piece != null && piece.getColor() == activeColor) {
                moves.addAll(piece.generateMoves());
            }
        }
        // TODO: filter the pseudo legal moves to make them completely legal
        return moves;
    }

    public void makeMove(Move move) {
        if (!isLegalSquare(move.from)) {
            throw new IllegalArgumentException("The source square of the move is outside of the board");
        }
        if (!isLegalSquare(move.to)) {
            throw new IllegalArgumentException("The target square of the move is outside of the board");
        }

        Piece piece = this.getPiece(move.from);
        if (piece == null) {
            throw new IllegalArgumentException("There is no piece on the source square of the move");
        }
        if (!piece.canMakeMove(move)) {
            throw new IllegalArgumentException("Illegal move");
        }

        squares[move.to.rank][move.to.file] = piece;
        squares[move.from.rank][move.from.file] = null;
        activeColor = activeColor.getInverse();
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

    public boolean isLegalSquare(int file, int rank) {
        return (0 <= rank && rank < Board.HEIGHT)
                && (0 <= file && file < Board.WIDTH);
    }

    public boolean isLegalSquare(Square pos) {
        return isLegalSquare(pos.rank, pos.file);
    }
}

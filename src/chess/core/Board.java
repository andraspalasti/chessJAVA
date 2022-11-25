package chess.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import chess.core.PGNParser.InvalidPGNException;

public class Board {
    private static final String STARTING_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";
    public static final int WIDTH = 8, HEIGHT = 8;

    private static final byte whiteKingsideMask = (byte) 0b0001,
            whiteQueensideMask = (byte) 0b0010,
            blackKingsideMask = (byte) 0b0100,
            blackQueensideMask = (byte) 0b1000;

    private byte castlingRights;
    protected Piece[][] squares;
    protected PieceColor activeColor;
    protected List<Move> moveHistory;

    public Board() {
        mustLoadFEN(STARTING_POS);
    }

    /**
     * Resets the board as if a whole new game has started.
     */
    public void reset() {
        mustLoadFEN(STARTING_POS);
    }

    /**
     * Returns the piece that is on the specified square of the board.
     * 
     * @param square The square to return the piece from
     * @return The piece that is on the square, if there is no piece on the square
     *         null will be returned
     */
    public Piece getPiece(Square square) {
        return getPiece(square.rank, square.file);
    }

    /**
     * Returns the piece from the specified row and column. The square a8 is on the
     * 0th row and 0th column.
     * 
     * @param row The row of the square
     * @param col The column of the square
     * @return The piece on the square, returns null if there is no piece on the
     *         square
     */
    public Piece getPiece(int row, int col) {
        if (row < 0 || HEIGHT <= row)
            throw new IllegalArgumentException("Row out of bounds");

        if (col < 0 || WIDTH <= col)
            throw new IllegalArgumentException("Column out of bounds");

        return squares[row][col];
    }

    /**
     * Returns the number of moves that have been played.
     * 
     * @return Number of moves that have been played
     */
    public int getMoveCount() {
        return moveHistory.size();
    }

    /**
     * Returns all the legal moves at the current state of the board.
     * 
     * @return List of all of the legal moves
     */
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

    /**
     * Checks if this is a legal move, than plays the move on the board.
     * 
     * @param move The move to play
     * @throws IllegalMove If the move is not legal it gets thrown
     */
    public void makeMove(Move move) throws IllegalMove {
        if (!isLegalSquare(move.from))
            throw new IllegalMove("The source square of the move is outside the board");
        if (!isLegalSquare(move.to))
            throw new IllegalMove("The destination square of the move is outside the board");

        Piece movedPiece = getPiece(move.from);
        if (movedPiece == null)
            throw new IllegalMove("There is no piece to move on the source square of the move");
        if (!movedPiece.canMakeMove(move))
            throw new IllegalMove("Illegal move for piece");

        move.setMovedPiece(movedPiece);
        if (move.isPromotion() && move.getPromotionTo() == null)
            throw new IllegalMove("The move is a promoting one but no promotion piece type was set");
        if (!isLegal(move))
            throw new IllegalMove("The move provided is just pseudo legal");
        mustMakeMove(move);
    }

    /**
     * Plays the specified move on the board. The move must be playable or it will
     * cause undefined behaviour.
     * 
     * @param move The move to play
     */
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

        activeColor = activeColor.getInverse();
        moveHistory.add(move);
    }

    /**
     * Undos the last move that has been played on the board.
     */
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
    }

    /**
     * Returns the latest move that has been made.
     * 
     * @return Last move that has been made or null if no moves have been played
     */
    public Move getLastMove() {
        if (0 < moveHistory.size()) {
            return moveHistory.get(moveHistory.size() - 1);
        }
        return null;
    }

    /**
     * Returns all the moves that have been played.
     * 
     * @return All of the move that have been played.
     */
    public Move[] getMoves() {
        Move[] moves = new Move[moveHistory.size()];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = moveHistory.get(i);
        }
        return moves;
    }

    /**
     * Returns the currently active color.
     * 
     * @return The active color
     */
    public PieceColor getActiveColor() {
        return activeColor;
    }

    /**
     * Resets the board than loads the moves described by the PGN format.
     * 
     * @param pgn The string that contains the PGN formatted moves.
     * @throws InvalidPGNException
     */
    public void loadPGN(String pgn) throws InvalidPGNException {
        reset();
        PGNParser.loadPGN(this, pgn);
    }

    /**
     * Writes the moves that have been played on the board to the specified output
     * described by the PGN format.
     * 
     * @param pw The output to write to
     */
    public void writeMoves(PrintWriter pw) {
        PGNParser.writePGN(pw, this);
    }

    /**
     * Decides whether the player with the specified color can castle kingside.
     * 
     * @param color The color of the player
     * @return The kingside castling availability of the specified player
     */
    public boolean canCastleKingside(PieceColor color) {
        return (castlingRights & (color == PieceColor.WHITE ? whiteKingsideMask : blackKingsideMask)) != 0;
    }

    /**
     * Decides whether the player with the specified color can castle queenside.
     * 
     * @param color The color of the player
     * @return The queenside castling availability of the specified player
     */
    public boolean canCastleQueenside(PieceColor color) {
        return (castlingRights & (color == PieceColor.WHITE ? whiteQueensideMask : blackQueensideMask)) != 0;
    }

    /**
     * Checks if the specified square is in the board.
     * 
     * @param row The row of the specified square
     * @param col The column of the specified square
     * @return True if the square is in the board
     */
    public boolean isLegalSquare(int row, int col) {
        return (0 <= col && col < Board.HEIGHT)
                && (0 <= row && row < Board.WIDTH);
    }

    /**
     * Checks if the specified square is in the board.
     * 
     * @param pos The square to test.
     * @return True if the square is in the board
     */
    public boolean isLegalSquare(Square pos) {
        return isLegalSquare(pos.rank, pos.file);
    }

    @Override
    public boolean equals(Object obj) {
        Board other = (Board) obj;
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            Piece p1 = squares[i / WIDTH][i % WIDTH];
            Piece p2 = other.squares[i / WIDTH][i % WIDTH];
            if (p1 != p2 && !p1.equals(p2)) {
                return false;
            }
        }
        return castlingRights == other.castlingRights && activeColor == other.activeColor;
    }

    /**
     * Finds the square that the specified piece is standing on.
     * 
     * @param p The piece to find
     * @return The square that the piece is located on
     */
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

    /**
     * This loads a position from a FEN string, exceptions are not handled so you
     * have to know beforehand that your fen string is perfectly correct.
     * 
     * @param fen The string to load the position from described by the FEN notation
     */
    protected void mustLoadFEN(String fen) {
        // Initalize default values
        this.squares = new Piece[HEIGHT][WIDTH];
        this.activeColor = PieceColor.WHITE;
        this.castlingRights = 0b0000;
        this.moveHistory = new ArrayList<>();

        String[] parts = fen.split("\\s");

        // Parse position
        String pos = parts[0];
        int row = 0, col = 0;
        for (char c : pos.toCharArray()) {
            if (Character.isDigit(c)) {
                col += (int) (c - '0');
            } else if (c == '/') {
                col = 0;
                row++;
            } else {
                PieceType type = PieceType.fromCharacter(c);
                PieceColor color = Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK;
                squares[row][col] = createPiece(type, color);
                col++;
            }
        }

        // Parse active color
        if (parts[1].contains("w")) {
            activeColor = PieceColor.WHITE;
        } else {
            activeColor = PieceColor.BLACK;
        }

        // Parse castling rights
        castlingRights = 0;
        String rights = parts[2];
        if (rights.contains("K"))
            castlingRights |= whiteKingsideMask;
        if (rights.contains("Q"))
            castlingRights |= whiteQueensideMask;
        if (rights.contains("k"))
            castlingRights |= blackKingsideMask;
        if (rights.contains("q"))
            castlingRights |= blackQueensideMask;
    }

    /**
     * A method to decide if a move is not just pseudo legal but actually legal.
     * (This means that the move can be played and the player's king will not be in
     * check)
     * 
     * @param move The move to test
     * @return True if the move is legal else it's false
     */
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

    /**
     * Returns an instance of the specified piece bound to this board.
     * 
     * @param type  The type of the piece
     * @param color The color of the piece
     * @return Instance of the specified piece
     */
    private Piece createPiece(PieceType type, PieceColor color) {
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

    public class IllegalMove extends Exception {
        public IllegalMove(String reason) {
            super(reason);
        }
    }
}

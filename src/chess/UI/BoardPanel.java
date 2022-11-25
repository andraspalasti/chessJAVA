package chess.UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import chess.core.Board;
import chess.core.Move;
import chess.core.Piece;
import chess.core.PieceColor;
import chess.core.PieceType;
import chess.core.Square;
import chess.core.Board.IllegalMove;
import chess.core.PGNParser.InvalidPGNException;

public class BoardPanel extends JPanel {
    private Board board;
    private SquareComponent[][] squares;
    private List<Move> legalMoves;
    private Square selected;

    private ActionListener onSquareClick = (event) -> {
        SquareComponent square = (SquareComponent) event.getSource();

        Piece piece = square.getPiece();
        if (square.isTarget()) {
            Move move = new Move(selected, square.getPos());
            try {
                makeMove(move);
            } catch (IllegalMove e) {
                e.printStackTrace();
            }
        } else if (piece != null) {
            setSelectedSquare(square.getPos());
        } else {
            setSelectedSquare(null);
        }
    };

    public BoardPanel() {
        this.board = new Board();
        this.legalMoves = board.generateMoves();
        this.squares = new SquareComponent[Board.HEIGHT][Board.WIDTH];
        this.setLayout(new GridLayout(Board.HEIGHT, Board.WIDTH));

        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                squares[row][col] = new SquareComponent(new Square(row, col));
                squares[row][col].setPiece(board.getPiece(row, col));
                squares[row][col].addActionListener(onSquareClick);
                this.add(squares[row][col]);
            }
        }
    }

    @Override
    public Insets getInsets() {
        Dimension size = getSize();
        int min = size.width < size.height ? size.width : size.height;
        return new Insets((size.height - min) / 2, (size.width - min) / 2,
                (size.height - min) / 2,
                (size.width - min) / 2);
    }

    /**
     * Plays the specified move on the board and updates the panel to reflect the
     * position after the move. Also if the move is a promoting one and there than
     * it opens a PieceChooser to let the user choose the piece to promote to.
     * 
     * @param move The move to play
     * @throws IllegalMove If the move is not legal
     */
    public void makeMove(Move move) throws IllegalMove {
        // Check if the move is a promoting one
        int promotionRow = board.getActiveColor() == PieceColor.WHITE ? 0 : Board.HEIGHT - 1;
        Piece movedPiece = board.getPiece(move.from);
        if (movedPiece != null && movedPiece.getType() == PieceType.Pawn && move.to.rank == promotionRow) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            PieceChooser pieceChooser = new PieceChooser(topFrame, board.getActiveColor());
            PieceType type = pieceChooser.showChooser();
            move.setPromotionTo(type);
        }

        board.makeMove(move);
        legalMoves = board.generateMoves();
        this.firePropertyChange("board", null, null);
        updateBoard();
    }

    /**
     * Undos the last move.
     */
    public void undo() {
        if (0 < board.getMoveCount()) {
            board.undoMove();
            legalMoves = board.generateMoves();
            this.firePropertyChange("board", null, null);
            updateBoard();
        }
    }

    /**
     * Loads the specified PGN.
     * 
     * @param pgn The PGN to load.
     * @throws InvalidPGNException If there is a problem with processing the PGN.
     */
    public void loadPGN(String pgn) throws InvalidPGNException {
        board.loadPGN(pgn);
        updateBoard();
        legalMoves = board.generateMoves();
        this.firePropertyChange("board", null, null);
    }

    /**
     * Returns all the moves that have been played.
     * 
     * @return All of the move that have been played.
     */
    public Move[] getMoves() {
        return board.getMoves();
    }

    /**
     * Returns the number of moves that have been played.
     * 
     * @return Number of moves that have been played
     */
    public int getMoveCount() {
        return board.getMoveCount();
    }

    /**
     * Saves the moves played on the board to the specified file with the PGN
     * format.
     * 
     * @param file The file to save to
     * @throws IOException If there is a problem with writing to the file
     */
    public void saveMoves(File file) throws IOException {
        PrintWriter pw = new PrintWriter(file);
        board.writeMoves(pw);
        pw.flush();
        pw.close();
    }

    /**
     * Updates the UI according to the current board posititon.
     */
    private void updateBoard() {
        setSelectedSquare(null);
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                squares[row][col].setPiece(board.getPiece(row, col));
                squares[row][col].setSelected(false);
            }
        }

        Move lastMove = board.getLastMove();
        if (lastMove != null) {
            squares[lastMove.from.getRow()][lastMove.from.getCol()].setSelected(true);
            squares[lastMove.to.getRow()][lastMove.to.getCol()].setSelected(true);
        }
        this.repaint();
    }

    /**
     * Changes the selected square to the specified one.
     * 
     * @param square The new selected square if null no square will be selected
     */
    private void setSelectedSquare(Square square) {
        // clear all targets
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                squares[row][col].setTarget(false);
            }
        }

        // clear previous selection if there was one
        if (selected != null) {
            squares[selected.rank][selected.file].setSelected(false);
        }

        selected = square;

        // Set the selected and the target squares
        if (selected != null) {
            squares[selected.rank][selected.file].setSelected(true);
            legalMoves.forEach((move) -> {
                if (move.from.equals(selected)) {
                    squares[move.to.rank][move.to.file].setTarget(true);
                }
            });
        }

        this.repaint();
    }
}

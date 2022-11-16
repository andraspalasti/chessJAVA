package chess.UI;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import chess.core.Board;
import chess.core.Move;
import chess.core.Piece;
import chess.core.Square;

public class BoardPanel extends JPanel {
    private Board board;
    private SquareComponent[][] squares;
    private List<Move> legalMoves;
    private Square selected;

    private ActionListener onSquareClick = (event) -> {
        SquareComponent square = (SquareComponent) event.getSource();

        Piece piece = square.getPiece();
        if (square.isTarget()) {
            makeMove(new Move(selected, square.getPos()));
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

    public void makeMove(Move move) {
        board.makeMove(move);
        legalMoves = board.generateMoves();
        fireBoardChanged();
    }

    private void fireBoardChanged() {
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

    public void undo() {
        if (0 < board.getMoveCount()) {
            board.undoMove();
            legalMoves = board.generateMoves();
            fireBoardChanged();
        }
    }
}

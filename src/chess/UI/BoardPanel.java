package chess.UI;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import chess.core.Board;
import chess.core.Move;
import chess.core.Piece;
import chess.core.Square;

// TODO: Need to clean up logic its a mess now
public class BoardPanel extends JPanel {
    private Board board;
    private SquareComponent[][] squares;
    private Square selected;
    private List<Move> legalMoves;

    private ActionListener onSquareClick = (event) -> {
        SquareComponent square = (SquareComponent) event.getSource();

        Piece piece = square.getPiece();
        if (square.isTarget()) {
            this.makeMove(new Move(selected, square.getPos()));
        } else if (piece != null) {
            this.setSelectedSquare(square.getPos());
        } else {
            this.setSelectedSquare(null);
        }

        this.repaint();
    };

    public BoardPanel() {
        this.board = new Board();
        this.legalMoves = board.generateMoves();
        this.squares = new SquareComponent[Board.HEIGHT][Board.WIDTH];
        this.setLayout(new GridLayout(Board.HEIGHT, Board.WIDTH));

        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                this.squares[row][col] = new SquareComponent(new Square(row, col));
                this.squares[row][col].setPiece(board.getPiece(row, col));
                this.squares[row][col].addActionListener(onSquareClick);
                this.add(this.squares[row][col]);
            }
        }
    }

    @Override
    public Insets getInsets() {
        Dimension size = getSize();
        int min = size.width < size.height ? size.width : size.height;
        return new Insets((size.height - min) / 2, (size.width - min) / 2, (size.height - min) / 2,
                (size.width - min) / 2);
    }

    private void makeMove(Move move) {
        this.board.makeMove(move);
        this.legalMoves = board.generateMoves();
        updateSquares();
        setSelectedSquare(null);
    }

    private void updateSquares() {
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                this.squares[row][col].setPiece(board.getPiece(row, col));
                this.squares[row][col].setSource(false);
                this.squares[row][col].setDestination(false);
            }
        }
        Move lastMove = board.getLastMove();
        if (lastMove != null) {
            this.squares[lastMove.from.getRow()][lastMove.from.getCol()].setSource(true);
            this.squares[lastMove.to.getRow()][lastMove.to.getCol()].setDestination(true);
        }
    }

    private void setSelectedSquare(Square square) {
        // clear all targets
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                squares[row][col].setTarget(false);
            }
        }

        // clear previous selection if there was one
        if (this.selected != null) {
            this.squares[selected.rank][selected.file].setSelected(false);
        }

        this.selected = square;

        // Set the selected and the target squares
        if (selected != null) {
            this.squares[selected.rank][selected.file].setSelected(true);
            legalMoves.forEach((move) -> {
                if (move.from.equals(selected)) {
                    squares[move.to.rank][move.to.file].setTarget(true);
                }
            });
        }
    }
}

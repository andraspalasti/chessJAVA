package chess.core;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import chess.core.Board.IllegalMove;

public class MoveTest {
    Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test(expected = IllegalMove.class)
    public void invalidSquareTest() throws IllegalMove {
        board.makeMove(new Move(new Square(-1, -1), new Square(10, 10)));
    }

    @Test(expected = IllegalMove.class)
    public void noPieceToMoveTest() throws IllegalMove {
        board.makeMove(new Move(new Square(4, 4), new Square(5, 5)));
    }

    @Test(expected = IllegalMove.class)
    public void illegalMoveTest() throws IllegalMove {
        board.makeMove(new Move(new Square("e2"), new Square("e5")));
    }

    @Test
    public void undoTest() throws IllegalMove {
        int numMoves = 10;
        for (int i = 0; i < numMoves; i++)
            board.makeMove(board.generateMoves().get(0));

        for (int i = 0; i < numMoves; i++)
            board.undoMove();

        assertTrue(board.equals(new Board()));
    }
}

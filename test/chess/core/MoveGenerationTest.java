package chess.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class MoveGenerationTest {
    private Board board;
    private PieceType type;
    private int numMoves;

    public MoveGenerationTest(PieceType type, int numMoves) {
        this.type = type;
        this.numMoves = numMoves;
    }

    @Before
    public void setUp() {
        board = new Board();
        for (int i = 0; i < board.squares.length; i++) {
            for (int j = 0; j < board.squares[i].length; j++) {
                board.squares[i][j] = null;
            }
        }
    }

    @Test
    public void runTest() {
        Piece p = createPiece(type, PieceColor.WHITE);
        board.squares[Board.HEIGHT / 2][Board.WIDTH / 2] = p;
        List<Move> moves = p.generateMoves();
        assertEquals(numMoves, moves.size());
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays
                .asList(new Object[][] { { PieceType.King, 8 + 2 }, { PieceType.Queen, 27 }, { PieceType.Rook, 14 },
                        { PieceType.Bishop, 13 }, { PieceType.Knight, 8 }, { PieceType.Pawn, 1 } });
    }

    private Piece createPiece(PieceType type, PieceColor color) {
        switch (type) {
            case King:
                return new King(board, color);
            case Queen:
                return new Queen(board, color);
            case Rook:
                return new Rook(board, color);
            case Bishop:
                return new Bishop(board, color);
            case Knight:
                return new Knight(board, color);
            case Pawn:
                return new Pawn(board, color);
            default:
                return null;
        }
    }
}

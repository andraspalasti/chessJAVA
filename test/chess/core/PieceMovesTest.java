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
public class PieceMovesTest {
    private Board board;
    private PieceType type;
    private int numMoves;

    public PieceMovesTest(PieceType type, int numMoves) {
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
        String fen = String.format("8/8/8/8/4%c3/8/8/8 w -", pieceToFEN(type, PieceColor.WHITE));
        board.mustLoadFEN(fen);
        Piece p = board.getPiece(new Square("e4"));
        List<Move> moves = p.generateMoves();
        assertEquals(numMoves, moves.size());
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays
                .asList(new Object[][] { { PieceType.King, 8 }, { PieceType.Queen, 27 }, { PieceType.Rook, 14 },
                        { PieceType.Bishop, 13 }, { PieceType.Knight, 8 }, { PieceType.Pawn, 1 } });
    }

    private char pieceToFEN(PieceType type, PieceColor color) {
        boolean isWhite = color == PieceColor.WHITE;
        switch (type) {
            case King:
                return isWhite ? 'K' : 'k';
            case Queen:
                return isWhite ? 'Q' : 'q';
            case Rook:
                return isWhite ? 'R' : 'r';
            case Bishop:
                return isWhite ? 'B' : 'b';
            case Knight:
                return isWhite ? 'N' : 'n';
            case Pawn:
                return isWhite ? 'P' : 'p';
            default:
                return ' ';
        }
    }
}

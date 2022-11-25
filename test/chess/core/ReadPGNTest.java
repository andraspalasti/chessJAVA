package chess.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import chess.core.PGNParser.InvalidPGNException;

@RunWith(value = Parameterized.class)
public class ReadPGNTest {
    private Board board;

    private String pgn;
    private String boardStr;

    public ReadPGNTest(String pgn, String boardStr) {
        this.pgn = pgn;
        this.boardStr = boardStr;
    }

    @Before
    public void setUp() throws InvalidPGNException {
        board = new Board();
        board.loadPGN(pgn);
    }

    @Test
    public void runTest() {
        int row = 0, col = 0;
        for (char c : boardStr.toCharArray()) {
            if (Character.isDigit(c)) {
                int emptySquares = c - '0';
                for (int i = col; i < col + emptySquares; i++) {
                    assertEquals(board.squares[row][col], null);
                }
                col += emptySquares;
            } else if (c == '/') {
                row++;
                col = 0;
            } else {
                PieceType type = PieceType.fromCharacter(c);
                assertNotEquals(board.squares[row][col], null);
                assertEquals(board.squares[row][col].getType(), type);
                assertEquals(board.squares[row][col].getColor(),
                        Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK);
                col++;
            }
        }
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays
                .asList(new Object[][] {
                        { "1. d4 d5 2. c4 dxc4 3. Nf3 Nf6 4. Qa4+ Nbd7 5. g3 a6 6. Qxc4 b5 7. Qc2 Bb7 8. Bg2 e6 9. O-O c5 10. a4 Rc8 11. axb5 axb5 12. Qb3 Qb6 13. Rd1 Bxf3 14. Qxf3 cxd4 15. Ra8 Rb8 16. Bd2 Bd6 17. Rxb8+ Nxb8 18. Na3 O-O 19. Qb3 Bxa3 20. bxa3 Nc6 21. a4 Na7 22. Rb1 Rb8 23. Bf4 Rc8 24. e3 Ng4 25. Bf3 Rc3 26. Qa2 e5 27. Bg5 h6 28. Be7 Nf6 29. Bb4 Rc8 30. exd4 Qxd4 31. a5 e4 32. Be2 Nd5 33. Be1 Nc3 34. Bxc3 Rxc3 35. Rd1 Qf6 36. Qd5 Qe6 37. Qa8+ Nc8 38. a6 Rc2 39. a7 Kh7 40. Bxb5 Qf5 41. Rf1 Nxa7 42. Ba4 Rc7 43. Re1 Nc8 44. Qxe4",
                                "2n5/2r2ppk/7p/5q2/B3Q3/6P1/5P1P/4R1K1" },
                        { "1. e4 e6 2. d4 d5 3. Nc3 Nf6 4. Bg5 dxe4 5. Nxe4 Nbd7 6. Nf3 Be7 7. Nxf6+ Nxf6 8. Bd3 b6 9. Qe2 Bb7 10. O-O O-O 11. Rad1 Qd5 12. c4 Qa5 13. Ne5 Rad8 14. a3 c5 15. d5 exd5 16. Ng4 Nxg4 17. Bxe7 Rde8 18. Qxg4 Rxe7 19. Bxh7+ Kh8 20. Qh4 Re6 21. Bg6+",
                                "5r1k/pb3pp1/1p2r1B1/q1pp4/2P4Q/P7/1P3PPP/3R1RK1" }
                });
    }
}

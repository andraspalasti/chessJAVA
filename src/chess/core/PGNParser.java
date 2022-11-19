package chess.core;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNParser {
    private static final Pattern fullMovePattern = Pattern.compile("([0-9]+)\\.\s(.*?)(?:$|\s(.*?)(?:\s|$))");
    private static final Pattern movePattern = Pattern.compile("^([KQRBN]?)([a-h]?)x?([a-h][1-8])$");

    // Castling
    private static final String kingsideCastle = "O-O";
    private static final String queensideCastle = "O-O-O";

    private static final String draw = "1/2-1/2";

    public static void loadPGN(Board board, String pgn) throws Exception {
        Matcher fullMoveMatcher = fullMovePattern.matcher(pgn);

        int moveCount = 1;
        while (fullMoveMatcher.find()) {
            // Check if the move has the correct number
            int moveNumber = Integer.parseInt(fullMoveMatcher.group(1));
            if (moveCount != moveNumber) {
                throw new Exception(String.format("Invalid pgn: Move number '%d' should be '%d' instead", moveNumber,
                        moveCount));
            }

            String whiteMove = fullMoveMatcher.group(2).trim();
            if (whiteMove.equals(draw))
                break;
            Move move1 = parseMove(board.generateMoves(), whiteMove);
            if (move1 == null)
                throw new Exception(String.format("Invalid pgn: Illegal move on move number %d", moveCount));
            board.makeMove(move1);

            String blackMove = fullMoveMatcher.group(3).trim();
            if (blackMove.equals(draw))
                break;

            Move move2 = parseMove(board.generateMoves(), blackMove);
            if (move2 == null)
                break;
            board.makeMove(move2);

            moveCount++;
        }
    }

    private static Move parseMove(List<Move> legalMoves, String move) {
        if (move == null || move.equals("")) {
            return null;
        }

        if (move.equals(draw)) {
            return null;
        } else if (move.equals(kingsideCastle)) {
            return legalMoves.stream().filter((m) -> m.isKingsideCastle()).findFirst().orElse(null);
        } else if (move.equals(queensideCastle)) {
            return legalMoves.stream().filter((m) -> m.isQueensideCastle()).findFirst().orElse(null);
        }

        Matcher moveMatcher = movePattern.matcher(move);
        if (!moveMatcher.find()) {
            return null;
        }

        PieceType type = moveMatcher.group(1).equals("") ? PieceType.Pawn
                : PieceType.fromCharacter(moveMatcher.group(1).charAt(0));
        int col = moveMatcher.group(2).equals("") ? -1 : Integer.parseInt(moveMatcher.group(2));
        Square target = new Square(moveMatcher.group(3));

        return legalMoves.stream().filter((m) -> {
            if (m.getMovedPiece().getType() != type) {
                return false;
            }
            if (col != -1 && m.from.file != col) {
                return false;
            }
            return m.to.equals(target);
        }).findFirst().orElse(null);
    }
}

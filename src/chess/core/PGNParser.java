package chess.core;

import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNParser {
    // Regex patterns
    private static final Pattern fullMovePattern = Pattern.compile("([0-9]+)\\.\s(.*?)(?:\s(.*?)(?:$|\s)|$)");
    private static final Pattern movePattern = Pattern.compile("([KQRBN]?)([a-h]?)x?([a-h][1-8])((?:=[QRBN])?)");

    // Castling
    private static final String kingsideCastle = "O-O";
    private static final String queensideCastle = "O-O-O";

    private static final String draw = "1/2-1/2";

    /**
     * Loads playes the moves specified in the PGN string on the board.
     * 
     * @param board The board to play the moves on
     * @param pgn The string that contains the moves in the PGN format
     * @throws InvalidPGNException If the string contains errors
     */
    public static void loadPGN(Board board, String pgn) throws InvalidPGNException {
        pgn = pgn.replaceAll("(\n\r|\n|\t)", " ");
        int moveCount = 1;
        Matcher fullMoveMatcher = fullMovePattern.matcher(pgn);
        while (fullMoveMatcher.find()) {
            // Check if the move has the correct number
            int moveNumber = Integer.parseInt(fullMoveMatcher.group(1));
            if (moveCount != moveNumber) {
                throw new InvalidPGNException(
                        String.format("Could not find move with move number: %d", moveCount));
            }

            try {
                String whiteMove = fullMoveMatcher.group(2).trim();
                Move move1 = parseMove(board.generateMoves(), whiteMove);
                if (move1 == null)
                    break;
                board.makeMove(move1);

                String blackMove = fullMoveMatcher.group(3).trim();
                Move move2 = parseMove(board.generateMoves(), blackMove);
                if (move2 == null)
                    break;
                board.makeMove(move2);
            } catch (InvalidPGNException e) {
                throw new InvalidPGNException(String.format("%s with move number: %d", e.getMessage(), moveCount));
            }

            moveCount++;
        }
    }

    /**
     * Returns the move from the legal moves that matches the described move by the string.
     * 
     * @param legalMoves The legal moves in that position
     * @param moveStr The string that describes the move
     * @return The move that matched the description
     * @throws InvalidPGNException If the described move is not in the legal moves or it is not the correct format
     */
    private static Move parseMove(List<Move> legalMoves, String moveStr) throws InvalidPGNException {
        if (moveStr == null || moveStr.equals("")) {
            return null;
        }

        if (moveStr.equals(draw)) {
            return null;
        } else if (moveStr.equals(kingsideCastle)) {
            return legalMoves.stream().filter((m) -> m.isKingsideCastle()).findFirst()
                    .orElseThrow(() -> new InvalidPGNException("Illegal move"));
        } else if (moveStr.equals(queensideCastle)) {
            return legalMoves.stream().filter((m) -> m.isQueensideCastle()).findFirst()
                    .orElseThrow(() -> new InvalidPGNException("Illegal move"));
        }

        Matcher moveMatcher = movePattern.matcher(moveStr);
        if (!moveMatcher.find()) {
            throw new InvalidPGNException("Invalid format for move");
        }

        PieceType type = moveMatcher.group(1).equals("") ? PieceType.Pawn
                : PieceType.fromCharacter(moveMatcher.group(1).charAt(0));
        int col = moveMatcher.group(2).equals("") ? -1 : moveMatcher.group(2).charAt(0) - 'a';
        Square target = new Square(moveMatcher.group(3));
        PieceType promoteTo = moveMatcher.group(4).equals("") ? null
                : PieceType.fromCharacter(moveMatcher.group(4).charAt(1));

        Move move = legalMoves.stream().filter((m) -> {
            if (m.getMovedPiece().getType() != type) {
                return false;
            }
            if (col != -1 && m.from.file != col) {
                return false;
            }
            return m.to.equals(target);
        }).findFirst().orElseThrow(() -> new InvalidPGNException("Illegal move"));
        move.setPromotionTo(promoteTo);
        return move;
    }

    /**
     * Writes the moves out to the specified output in the PGN format.
     * 
     * @param pw The output to write to
     * @param board The board which moves to write
     */
    public static void writePGN(PrintWriter pw, Board board) {
        Move[] moves = board.getMoves();
        for (int i = 0; i < moves.length; i++) {
            Move move = moves[i];
            if (i % 2 == 0) {
                pw.write(Integer.toString(i / 2 + 1));
                pw.write(". ");
            }
            PieceType type = move.getMovedPiece().getType();
            if (type == PieceType.King && move.isKingsideCastle()) {
                pw.write(kingsideCastle);
            } else if (type == PieceType.King && move.isQueensideCastle()) {
                pw.write(queensideCastle);
            } else {
                pw.write(type.toString());
                pw.append((char) (move.from.getCol() + 'a'));
                if (move.getCapturedPiece() != null) {
                    pw.append('x');
                }
                pw.write(move.to.toString());
                if (move.isPromotion()) {
                    pw.append('=');
                    pw.write(move.getPromotionTo().toString());
                }
            }
            if (i % 2 == 0) {
                pw.append(' ');
            } else {
                pw.write(System.lineSeparator());
            }
        }
    }

    public static class InvalidPGNException extends Exception {
        public InvalidPGNException(String reason) {
            super(reason);
        }
    }
}

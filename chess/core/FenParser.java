package chess.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FenParser {
    private static final Map<Character, PieceType> symbolToPieceType = new HashMap<>();
    static {
        symbolToPieceType.put('p', PieceType.Pawn);
        symbolToPieceType.put('n', PieceType.Knight);
        symbolToPieceType.put('b', PieceType.Bishop);
        symbolToPieceType.put('r', PieceType.Rook);
        symbolToPieceType.put('q', PieceType.Queen);
        symbolToPieceType.put('k', PieceType.King);
    }

    public static void loadFen(Board board, String fen) throws InvalidFENException {
        Iterator<String> sections = Arrays.asList(fen.trim().split("\s")).iterator();

        // Parse board
        String squares = sections.next();
        int rank = 0, file = 0;
        for (int i = 0; i < squares.length(); i++) {
            char symbol = squares.charAt(i);
            if (symbol == '/') {
                file = 0;
                rank += 1;
            } else {
                int skip = Character.digit(symbol, 10);
                if (0 < skip) {
                    file += skip;
                    if (8 <= file && squares.charAt(i + 1) != '/')
                        throw new InvalidFENException("Board representation incorrect in the FEN.");
                } else {
                    PieceType type = symbolToPieceType.get(Character.toLowerCase(symbol));
                    if (type == null)
                        throw new InvalidFENException("Board representation incorrect in the FEN.");
                    PieceColor color = Character.isUpperCase(symbol) ? PieceColor.WHITE : PieceColor.BLACK;
                    board.squares[rank][file++] = createPiece(board, type, color);
                }
            }
        }

        // Parse active color
        if (!sections.hasNext())
            throw new InvalidFENException("There is no active color section in the FEN string");

        String activeColor = sections.next();
        if (activeColor.equals("w")) {
            board.activeColor = PieceColor.WHITE;
        } else if (activeColor.equals("b")) {
            board.activeColor = PieceColor.BLACK;
        } else {
            throw new InvalidFENException("Invalid active color");
        }

        // Parse castling rights
        if (!sections.hasNext())
            throw new InvalidFENException("There is no castling rights section in the FEN string");

        // String rights = sections.next();
        // board.whiteCastleQueenside = rights.contains("Q");
        // board.whiteCastleKingside = rights.contains("K");
        // board.blackCastleQueenside = rights.contains("q");
        // board.blackCastleKingside = rights.contains("k");

        // Parse en passant target square
        if (!sections.hasNext())
            throw new InvalidFENException("There is no en passant target square section in the FEN string");

        String square = sections.next();
        try {
            board.enPassantSquare = new Square(square);
        } catch (IllegalArgumentException e) {
            throw new InvalidFENException(String.format("Illegal en passant target square: '%s'", e.getMessage()));
        }
    }

    public static class InvalidFENException extends Exception {
        public InvalidFENException(String message) {
            super(message);
        }
    }

    private static Piece createPiece(Board board, PieceType type, PieceColor color) {
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

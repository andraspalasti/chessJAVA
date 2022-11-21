package chess.UI;

import chess.core.PieceColor;
import chess.core.PieceType;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class PieceImages {
    private static final int NUM_PIECES = 6;

    // Read in images for chess pieces
    private static final Map<PieceType, Integer> pieceTypeToCol = new HashMap<>();
    private static final Image[][] chessPieceImages = new Image[2][NUM_PIECES];
    static {
        pieceTypeToCol.put(PieceType.King, 0);
        pieceTypeToCol.put(PieceType.Queen, 1);
        pieceTypeToCol.put(PieceType.Bishop, 2);
        pieceTypeToCol.put(PieceType.Knight, 3);
        pieceTypeToCol.put(PieceType.Rook, 4);
        pieceTypeToCol.put(PieceType.Pawn, 5);

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            BufferedImage bi = ImageIO.read(classLoader.getResource("assets/pieces.png"));
            int width = bi.getWidth() / NUM_PIECES, height = bi.getHeight() / 2;
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < NUM_PIECES; col++) {
                    chessPieceImages[row][col] = bi.getSubimage(
                            col * width, row * height, width, height);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Image getImage(PieceType type, PieceColor color) {
        int row = color == PieceColor.WHITE ? 0 : 1;
        int col = pieceTypeToCol.get(type);
        return chessPieceImages[row][col];
    }
}

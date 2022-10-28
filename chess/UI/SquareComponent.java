package chess.UI;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import chess.core.Piece;
import chess.core.PieceColor;
import chess.core.PieceType;
import chess.core.Square;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class SquareComponent extends JButton {
    private static final Color black = new Color(181, 136, 99);
    private static final Color white = new Color(240, 217, 181);
    private static final Color selectedOnWhite = new Color(247, 236, 118);
    private static final Color selectedOnBlack = new Color(218, 195, 73);

    // Read in images for chess pieces
    private static final PieceType[] pieces = new PieceType[] { PieceType.King, PieceType.Queen, PieceType.Bishop,
            PieceType.Knight, PieceType.Rook, PieceType.Pawn };
    private static final Image[][] chessPieceImages = new Image[2][pieces.length];
    static {
        try {
            File img = new File("assets" + File.separator + "pieces.png");
            int width = 2000 / pieces.length, height = 668 / 2;
            BufferedImage bi = ImageIO.read(img);
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < pieces.length; col++) {
                    chessPieceImages[row][col] = bi.getSubimage(
                            col * width, row * height, width, height);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private final Square pos;
    private Piece piece;
    private boolean isTarget = false;

    public SquareComponent(Square pos) {
        this.pos = pos;
        this.setPreferredSize(new Dimension(60, 60));
    }

    public Piece getPiece() {
        return this.piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void paint(Graphics g) {
        Dimension size = getSize();
        this.setCursor(getSquareCursor());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.setColor(getSquareBackground());
        g2d.fillRect(0, 0, size.width, size.height);

        Image img = getPieceImage();
        if (img != null) {
            g2d.drawImage(img, 0, 0, size.width, size.height, null);
        }

        if (isTarget) {
            g2d.setColor(new Color(63, 63, 63, 255 / 3));
            g2d.fillOval(size.width / 3, size.height / 3, size.width / 3, size.height / 3);
        }
    }

    public boolean isTarget() {
        return this.isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }

    public Square getPos() {
        return this.pos;
    }

    private Cursor getSquareCursor() {
        if (this.piece != null || isTarget) {
            return new Cursor(Cursor.HAND_CURSOR);
        }
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }

    private Color getSquareBackground() {
        boolean isWhite = (pos.rank + pos.file) % 2 == 0;
        if (this.isSelected()) {
            return isWhite ? selectedOnWhite : selectedOnBlack;
        }
        return isWhite ? white : black;
    }

    private Image getPieceImage() {
        if (piece == null) {
            return null;
        }

        int row = piece.getColor() == PieceColor.WHITE ? 0 : 1;
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i] == piece.getType()) {
                return chessPieceImages[row][i];
            }
        }
        return null;
    }
}

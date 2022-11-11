package chess.UI;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import chess.core.Piece;
import chess.core.PieceColor;
import chess.core.PieceType;
import chess.core.Square;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;

// TODO: Need to clean up logic its a mess now
public class SquareComponent extends JButton {
    private Color backgroundColor;
    private Color highlightColor;

    // Read in images for chess pieces
    private static final PieceType[] pieces = new PieceType[] { PieceType.King, PieceType.Queen, PieceType.Bishop,
            PieceType.Knight, PieceType.Rook, PieceType.Pawn };
    private static final Image[][] chessPieceImages = new Image[2][pieces.length];
    static {
        try {
            File img = new File("assets" + File.separator + "pieces.png");
            BufferedImage bi = ImageIO.read(img);
            int width = bi.getWidth() / pieces.length, height = bi.getHeight() / 2;
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < pieces.length; col++) {
                    chessPieceImages[row][col] = bi.getSubimage(
                            col * width, row * height, width, height);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Square pos;
    private Piece piece;
    private boolean isTarget = false;
    private boolean isSource = false, isDestination = false;

    public SquareComponent(Square pos) {
        this.pos = pos;
        this.setPreferredSize(new Dimension(60, 60));

        if (pos.isWhite()) {
            backgroundColor = new Color(240, 217, 181);
            highlightColor = new Color(247, 236, 118);
        } else {
            backgroundColor = new Color(181, 136, 99);
            highlightColor = new Color(218, 195, 73);
        }
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

        if (this.piece != null || isTarget) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.isSelected() || isSource || isDestination) {
            g2d.setColor(highlightColor);
        } else {
            g2d.setColor(backgroundColor);
        }
        g2d.fillRect(0, 0, size.width, size.height);

        Image img = getPieceImage();
        if (img != null) {
            g2d.drawImage(img, 0, 0, size.width, size.height, null);
        }

        if (isTarget) {
            g2d.setColor(new Color(63, 63, 63, 255 / 3));
            if (this.piece == null) {
                g2d.fillOval(size.width / 3, size.height / 3, size.width / 3, size.height / 3);
            } else {
                Ellipse2D outer = new Ellipse2D.Double(1, 1, size.width - 2, size.height - 2);
                Ellipse2D inner = new Ellipse2D.Double(6, 6, size.width - 12, size.height - 12);
                Area circle = new Area(outer);
                circle.subtract(new Area(inner));
                g2d.fill(circle);
            }
        }
    }

    public boolean isTarget() {
        return this.isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }

    public void setSource(boolean isSource) {
        this.isSource = isSource;
    }

    public void setDestination(boolean isDestination) {
        this.isDestination = isDestination;
    }

    public Square getPos() {
        return this.pos;
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

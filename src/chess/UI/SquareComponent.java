package chess.UI;

import javax.swing.JButton;

import chess.core.Piece;
import chess.core.Square;

import java.awt.*;
import java.awt.geom.*;

public class SquareComponent extends JButton {
    private Color backgroundColor;
    private Color highlightColor;

    private Square pos;
    private Piece piece;
    private boolean isTarget = false;

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

    /**
     * Returns the piece which is standing on the square.
     * 
     * @return The piece standing on the square
     */
    public Piece getPiece() {
        return this.piece;
    }

    /**
     * Sets the piece that is standing on this square.
     * 
     * @param piece The piece which is standing on this square
     */
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

        if (this.isSelected()) {
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

    /**
     * Returns if the square is marked as a target square or not.
     * 
     * @return True if the square is marked as a target else false
     */
    public boolean isTarget() {
        return this.isTarget;
    }

    /**
     * Marks or clears the target flag for the square.
     * 
     * @param isTarget If true than marks the square as target else clears mark
     */
    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }

    /**
     * Returns the position of the square.
     * 
     * @return The position of the square
     */
    public Square getPos() {
        return this.pos;
    }

    /**
     * Returns the image that corresponds to the piece standing on the square.
     * 
     * @return The image of the piece or null if no piece is standing on the square
     */
    private Image getPieceImage() {
        if (piece == null) {
            return null;
        }
        return PieceImages.getImage(piece.getType(), piece.getColor());
    }
}

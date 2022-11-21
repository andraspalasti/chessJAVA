package chess.UI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FlowLayout;
import java.awt.Graphics;

import chess.core.PieceColor;
import chess.core.PieceType;

public class PieceChooser extends JDialog {
    private static final PieceType[] pieces = new PieceType[] { PieceType.Queen, PieceType.Rook, PieceType.Knight,
            PieceType.Bishop };

    private PieceType selected = null;

    public PieceChooser(JFrame parent, PieceColor color) {
        super(parent, true);
        this.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        this.setLayout(new FlowLayout());
        for (PieceType pieceType : pieces) {
            PieceButton button = new PieceButton(pieceType, color);
            button.setPreferredSize(new Dimension(80, 80));
            button.addActionListener((e) -> {
                selected = pieceType;
                this.setVisible(false);
            });
            this.add(button);
        }
        this.pack();
        this.setSize(getSize());
        this.setResizable(false);
    }

    public PieceType showChooser() {
        selected = null;
        this.setVisible(true);
        return selected;
    }

    private class PieceButton extends JButton {
        private PieceType type;
        private PieceColor color;

        public PieceButton(PieceType type, PieceColor color) {
            this.type = type;
            this.color = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension size = getSize();

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(PieceImages.getImage(type, color), 5, 5, size.width - 10, size.height - 10, null);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(60 * 8, 60 * 8));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);

        PieceChooser pc = new PieceChooser(frame, PieceColor.WHITE);
        System.out.println(pc.showChooser());
    }
}

package chess;

import javax.swing.JFrame;

import chess.UI.GamePanel;

public class Application {
    public static void main(String[] args) {
        JFrame frame = new JFrame();

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }
}

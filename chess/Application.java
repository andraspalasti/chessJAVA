package chess;

import javax.swing.JFrame;

import chess.UI.BoardPanel;

public class Application {
    public static void main(String[] args) {
        JFrame frame = new JFrame();

        BoardPanel boardPanel = new BoardPanel();
        frame.add(boardPanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }
}

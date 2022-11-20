package chess.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import chess.core.PGNParser.InvalidPGNException;

public class GamePanel extends JPanel {
    private BoardPanel boardPanel;
    private JButton backButton;
    private JButton forwardButton;

    ActionListener onBackButton = (e) -> {
        boardPanel.undo();
    };

    ActionListener onForwardButton = (e) -> {

    };

    public GamePanel() {
        boardPanel = new BoardPanel();

        backButton = new JButton("Move Back");
        backButton.addActionListener(onBackButton);

        forwardButton = new JButton("Move Forward");
        forwardButton.addActionListener(onForwardButton);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(backButton);
        bottomPanel.add(forwardButton);

        this.setLayout(new BorderLayout());
        this.add(boardPanel, BorderLayout.CENTER);
        this.add(new MoveHistoryPanel(boardPanel), BorderLayout.AFTER_LINE_ENDS);
    }

    public void saveGame(File file) throws IOException {
        boardPanel.saveToFile(file);
    }

    public void loadGame(String pgn) throws InvalidPGNException {
        boardPanel.loadPGN(pgn);
    }
}

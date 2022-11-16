package chess.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

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
        this.add(bottomPanel, BorderLayout.PAGE_END);
    }
}

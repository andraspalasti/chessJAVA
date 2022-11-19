package chess.UI;

import java.beans.PropertyChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

import chess.core.Move;

public class MoveHistoryPanel extends JPanel {
    private BoardPanel boardPanel;
    private DefaultListModel<Move> moveHistory = new DefaultListModel<>();
    private JList<Move> jlist;

    private PropertyChangeListener onBoardChange = (e) -> {
        if (boardPanel.getMoveCount() <= 0)
            return;

        Move[] moves = boardPanel.getMoves();
        int lastMoveIndex = moves.length - 1;
        if (lastMoveIndex < moveHistory.size()) {
            boolean isSame = moveHistory.get(lastMoveIndex).equals(moves[lastMoveIndex]);
            if (!isSame) {
                // history is changed
                moveHistory.removeRange(lastMoveIndex, moveHistory.size() - 1);
                moveHistory.addElement(moves[lastMoveIndex]);
                jlist.setSelectedIndex(lastMoveIndex);
            }
        } else {
            while (moveHistory.size() <= lastMoveIndex) {
                moveHistory.addElement(moves[moveHistory.size()]);
            }
            jlist.setSelectedIndex(lastMoveIndex);
        }
    };

    private ListSelectionListener onSelectionChange = (e) -> {
        int selected = jlist.getSelectedIndex() + 1;
        int moveCount = boardPanel.getMoveCount();
        if (moveCount < selected) {
            for (int i = 0; i < selected - moveCount; i++)
                boardPanel.makeMove(moveHistory.get(moveCount + i));
        } else {
            for (int i = 0; i < moveCount - selected; i++)
                boardPanel.undo();
        }
    };

    public MoveHistoryPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        this.boardPanel.addPropertyChangeListener("board", onBoardChange);

        this.setLayout(new BorderLayout());

        jlist = new JList<>(moveHistory);
        jlist.addListSelectionListener(onSelectionChange);
        this.add(new JScrollPane(jlist), BorderLayout.CENTER);

        JButton backButton = new JButton("Move Back");
        backButton.addActionListener((e) -> {
            int index = jlist.getSelectedIndex();
            if (0 <= index) {
                jlist.setSelectedIndex(index - 1);
                if (index == 0) {
                    boardPanel.undo();
                    jlist.setSelectedValue(null, false);
                }
            }
        });

        JButton forwardButton = new JButton("Move Forward");
        forwardButton.addActionListener((e) -> {
            int index = jlist.getSelectedIndex();
            if (index < moveHistory.size() - 1) {
                jlist.setSelectedIndex(index + 1);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(backButton);
        bottomPanel.add(forwardButton);
        this.add(bottomPanel, BorderLayout.PAGE_END);
    }
}

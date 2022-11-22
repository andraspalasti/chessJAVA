package chess;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import chess.UI.BoardPanel;
import chess.UI.MoveHistoryPanel;
import chess.core.PGNParser.InvalidPGNException;

public class Application extends JFrame {
    // private GamePanel gamePanel;
    private BoardPanel boardPanel;

    private ActionListener importGame = (event) -> {
        // Choose file to import
        JFileChooser chooser = new JFileChooser();
        int rVal = chooser.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            Path fp = Path.of(chooser.getSelectedFile().getAbsolutePath());
            try {
                String pgn = Files.readString(fp);
                boardPanel.loadPGN(pgn);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPGNException e) {
                e.printStackTrace();
            }
        }
    };

    private ActionListener exportGame = (event) -> {
        JFileChooser chooser = new JFileChooser();
        int rVal = chooser.showSaveDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                boardPanel.saveMoves(chooser.getSelectedFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public Application() {
        boardPanel = new BoardPanel();
        this.add(boardPanel, BorderLayout.CENTER);
        this.add(new MoveHistoryPanel(boardPanel), BorderLayout.LINE_END);

        JMenuItem imp = new JMenuItem("Import");
        imp.addActionListener(importGame);

        JMenuItem exp = new JMenuItem("Export");
        exp.addActionListener(exportGame);

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        mb.add(file);
        file.add(imp);
        file.add(exp);
        this.setJMenuBar(mb);
    }

    public static void main(String[] args) {
        Application app = new Application();

        app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        app.pack();
        app.setMinimumSize(app.getSize());
        app.setVisible(true);
    }
}

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

import chess.UI.GamePanel;
import chess.core.PGNParser.InvalidPGNException;

public class Application extends JFrame {
    private GamePanel gamePanel;

    private ActionListener importGame = (event) -> {
        // Choose file to import
        JFileChooser chooser = new JFileChooser();
        int rVal = chooser.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            Path fp = Path.of(chooser.getSelectedFile().getAbsolutePath());
            try {
                String pgn = Files.readString(fp);
                gamePanel.loadGame(pgn);
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
                gamePanel.saveGame(chooser.getSelectedFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public Application() {
        gamePanel = new GamePanel();
        this.add(gamePanel, BorderLayout.CENTER);

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

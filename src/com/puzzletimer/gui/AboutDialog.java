package com.puzzletimer.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.graphics.Panel3D;
import com.puzzletimer.graphics.algebra.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.ColorScheme.FaceColor;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.RubiksCube;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {
    public AboutDialog(JFrame owner, boolean modal) {
        super(owner, modal);

        setTitle("Puzzle Timer 0.3");
        setResizable(false);

        createComponents();
        pack();
    }

    private void createComponents() {
        setLayout(new MigLayout("", "", ""));

        // panel3D
        Panel3D panel3D = new Panel3D();
        panel3D.cameraPosition = new Vector3(0, 0, -4.5);
        Puzzle puzzle = new RubiksCube();
        FaceColor[] faceColors = {
            new FaceColor("FACE-B", "", null, new Color(255, 255, 255)),
            new FaceColor("FACE-D", "", null, new Color(255, 255, 255)),
            new FaceColor("FACE-F", "", null, new Color(255, 255, 255)),
            new FaceColor("FACE-L", "", null, new Color(255, 255, 255)),
            new FaceColor("FACE-R", "", null, new Color(255, 255, 255)),
            new FaceColor("FACE-U", "", null, new Color(255, 255, 255)),
        };
        panel3D.mesh = puzzle.getScrambledPuzzleMesh(
            new ColorScheme(null, faceColors),
            new String[] { });
        add(panel3D, "width 125, height 125, spany");

        // labelPuzzleTimer
        JLabel labelPuzzleTimer = new JLabel("Puzzle Timer 0.3");
        labelPuzzleTimer.setFont(new Font("Arial", Font.BOLD, 16));
        add(labelPuzzleTimer, "split 3, gapbottom 10, flowy");

        // labelURL
        JLabel labelURL = new JLabel("http://www.puzzletimer.com");
        add(labelURL);

        // labelWalter
        JLabel labelWalter = new JLabel("Walter Souza <walterprs@gmail.com>");
        add(labelWalter);
    }
}

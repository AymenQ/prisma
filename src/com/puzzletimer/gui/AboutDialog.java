package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.graphics.Panel3D;
import com.puzzletimer.graphics.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.ColorScheme.FaceColor;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.RubiksCube;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {
    public AboutDialog(JFrame owner, boolean modal) {
        super(owner, modal);

        setTitle(_("about.prisma_puzzle_timer") + " " + _("about.version"));
        setResizable(false);

        createComponents();
        pack();
    }

    private void createComponents() {
        setLayout(new MigLayout("", "", ""));

        // panel3D
        Puzzle puzzle = new RubiksCube();
        ColorScheme colorScheme =
            new ColorScheme(
                "RUBIKS-CUBE",
                new FaceColor[] {
                    new FaceColor("RUBIKS-CUBE", "FACE-B", null, new Color(255, 255, 255)),
                    new FaceColor("RUBIKS-CUBE", "FACE-D", null, new Color(255, 255, 255)),
                    new FaceColor("RUBIKS-CUBE", "FACE-F", null, new Color(255, 255, 255)),
                    new FaceColor("RUBIKS-CUBE", "FACE-L", null, new Color(255, 255, 255)),
                    new FaceColor("RUBIKS-CUBE", "FACE-R", null, new Color(255, 255, 255)),
                    new FaceColor("RUBIKS-CUBE", "FACE-U", null, new Color(255, 255, 255)),
                });

        Panel3D panel3D = new Panel3D();
        panel3D.setCameraPosition(new Vector3(0, 0, -4.5));
        panel3D.setMesh(puzzle.getScrambledPuzzleMesh(colorScheme, new String[] { }));
        add(panel3D, "width 125, height 125, spany");

        // labelPrismaPuzzleTimer
        JLabel labelPrismaPuzzleTimer = new JLabel(_("about.prisma_puzzle_timer") + " " + _("about.version"));
        labelPrismaPuzzleTimer.setFont(new Font("Arial", Font.BOLD, 16));
        add(labelPrismaPuzzleTimer, "split 3, gapbottom 10, flowy");

        // labelWalter
        JLabel labelContributors = new JLabel(_("about.contributors"));
        add(labelContributors);
    }
}

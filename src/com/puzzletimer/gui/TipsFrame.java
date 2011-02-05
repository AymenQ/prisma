package com.puzzletimer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ScrambleListener;
import com.puzzletimer.state.ScrambleManager;
import com.puzzletimer.tips.TipProvider;

@SuppressWarnings("serial")
public class TipsFrame extends JFrame {
    private JTextArea textAreaTips;
    private JButton buttonOk;

    public TipsFrame(
            final PuzzleProvider puzzleProvider,
            final TipProvider tipProvider,
            final ScramblerProvider scrambleProvider,
            final CategoryManager categoryManager,
            ScrambleManager scrambleManager) {
        super();

        setMinimumSize(new Dimension(480, 320));
        setPreferredSize(getMinimumSize());

        createComponents();

        // title
        categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                Scrambler scrambler = scrambleProvider.get(currentCategory.getScramblerId());
                Puzzle puzzle = puzzleProvider.get(scrambler.getScramblerInfo().getPuzzleId());
                setTitle("Tips - " + puzzle.getPuzzleInfo().getDescription());
            }
        });
        categoryManager.notifyListeners();

        // tips
        scrambleManager.addScrambleListener(new ScrambleListener() {
            @Override
            public void scrambleChanged(Scramble scramble) {
                Category category = categoryManager.getCurrentCategory();

                StringBuilder contents = new StringBuilder();
                for (String tipId : category.getTipIds()) {
                    contents.append(tipProvider.get(tipId).getTip(scramble));
                    contents.append("\n\n");
                }

                TipsFrame.this.textAreaTips.setText(contents.toString().trim());
                TipsFrame.this.textAreaTips.setCaretPosition(0);
            }
        });

        // ok button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                TipsFrame.this.setVisible(false);
            }
        });

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    TipsFrame.this.setVisible(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "",
                "[pref!][]16[pref!]"));

        // labelTips
        add(new JLabel("Tips"), "wrap");

        // textAreaContents
        this.textAreaTips = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(this.textAreaTips);
        add(scrollPane, "grow, wrap");

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "tag ok");
    }
}

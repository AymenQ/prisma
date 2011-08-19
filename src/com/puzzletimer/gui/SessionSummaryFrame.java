package com.puzzletimer.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Solution;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.SessionListener;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.BestAverage;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.Percentile;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.statistics.Worst;
import com.puzzletimer.util.SolutionUtils;
import com.puzzletimer.util.StringUtils;

@SuppressWarnings("serial")
public class SessionSummaryFrame extends JFrame {
    private JTextArea textAreaSummary;
    private JButton buttonCopyToClipboard;
    private JButton buttonOk;

    public SessionSummaryFrame(final CategoryManager categoryManager, SessionManager sessionManager) {
        super();

        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(getMinimumSize());

        createComponents();

        // title
        categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                setTitle("Session Summary - " + currentCategory.getDescription());
            }
        });
        categoryManager.notifyListeners();

        // summary
        sessionManager.addSessionListener(new SessionListener() {
            @Override
            public void solutionsUpdated(Solution[] solutions) {
                updateSummary(categoryManager.getCurrentCategory(), solutions);
            }
        });

        // copy to clipboard
        this.buttonCopyToClipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                StringSelection contents =
                    new StringSelection(SessionSummaryFrame.this.textAreaSummary.getText());
                Clipboard clipboard =
                    Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(contents, contents);
            }
        });

        // ok button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                SessionSummaryFrame.this.setVisible(false);
            }
        });

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    SessionSummaryFrame.this.setVisible(false);
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
                "[pref!][][pref!]16[pref!]"));

        // labelSessionSummary
        add(new JLabel("Summary"), "wrap");

        // textAreaContents
        this.textAreaSummary = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(this.textAreaSummary);
        add(scrollPane, "grow, wrap");

        // button copy to clipboard
        this.buttonCopyToClipboard = new JButton("Copy to Clipboard");
        add(this.buttonCopyToClipboard, "width 150, right, wrap");

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "tag ok");
    }

    private void updateSummary(Category currentCategory, Solution[] solutions) {
        StringBuilder summary = new StringBuilder();

        if (solutions.length >= 1) {
            // categoryName
            summary.append(currentCategory.getDescription());
            summary.append("\n");

            // session interval
            Date start = solutions[solutions.length - 1].getTiming().getStart();
            Date end = solutions[0].getTiming().getEnd();

            DateFormat dateTimeFormat =
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            DateFormat timeFormat =
                DateFormat.getTimeInstance(DateFormat.MEDIUM);

            summary.append(dateTimeFormat.format(start) + " - " + timeFormat.format(end));
            summary.append("\n");

            summary.append("\n");

            // statistics
            String[] labels = {
                "Mean:              ",
                "Average:           ",
                "Best Time:         ",
                "Median:            ",
                "Worst Time:        ",
                "Standard deviation:",
            };

            StatisticalMeasure[] statistics = {
                new Mean(1, Integer.MAX_VALUE),
                new Average(3, Integer.MAX_VALUE),
                new Best(1, Integer.MAX_VALUE),
                new Percentile(1, Integer.MAX_VALUE, 0.5),
                new Worst(1, Integer.MAX_VALUE),
                new StandardDeviation(1, Integer.MAX_VALUE),
            };

            int maxStringLength = 0;
            for (int i = 0; i < statistics.length; i++) {
                if (solutions.length < statistics[i].getMinimumWindowSize()) {
                    continue;
                }

                statistics[i].setSolutions(solutions);

                String s = SolutionUtils.format(statistics[i].getValue());
                if (s.length() > maxStringLength) {
                    maxStringLength = s.length();
                }
            }

            for (int i = 0; i < labels.length; i++) {
                if (solutions.length < statistics[i].getMinimumWindowSize()) {
                    continue;
                }

                summary.append(String.format(
                    "%s %" + maxStringLength + "s",
                    labels[i],
                    SolutionUtils.format(statistics[i].getValue())));
                summary.append("\n");
            }

            summary.append("\n");
        }

        // best average of X
        String[] labels = {
            "Best average of 5:",
            "Best average of 12:",
        };

        StatisticalMeasure[] statistics = {
            new BestAverage(5, Integer.MAX_VALUE),
            new BestAverage(12, Integer.MAX_VALUE),
        };

        for (int i = 0; i < statistics.length; i++) {
            int windowSize = statistics[i].getMinimumWindowSize();

            if (solutions.length >= windowSize) {
                statistics[i].setSolutions(solutions);
                int windowPosition = statistics[i].getWindowPosition();

                // value
                summary.append(labels[i] + " " + SolutionUtils.format(statistics[i].getValue()));
                summary.append("\n");

                // index range
                summary.append(String.format(
                    "  %d-%d - ",
                    solutions.length - windowPosition - windowSize + 1,
                    solutions.length - windowPosition));

                // find indices of best and worst times
                int indexBest = 0;
                int indexWorst = 0;
                long[] times = new long[windowSize];
                for (int j = 0; j < windowSize; j++) {
                    times[j] = SolutionUtils.realTime(solutions[windowPosition + j]);

                    if (times[j] < times[indexBest]) {
                        indexBest = j;
                    }

                    if (times[j] > times[indexWorst]) {
                        indexWorst = j;
                    }
                }

                // times
                String sTimes = "";
                for (int j = windowSize - 1; j >= 0; j--) {
                    if (j == indexBest || j == indexWorst) {
                        sTimes += "(" + SolutionUtils.format(times[j]) + ") ";
                    } else {
                        sTimes += SolutionUtils.format(times[j]) + " ";
                    }
                }

                summary.append(sTimes.trim());
                summary.append("\n");

                summary.append("\n");
            }
        }

        // solutions
        String[] sSolutions = new String[solutions.length];
        long[] realTimes = SolutionUtils.realTimes(solutions, false);

        int maxStringLength = 0;
        for (int i = 0; i < realTimes.length; i++) {
            sSolutions[i] = SolutionUtils.format(realTimes[i]);
            if (sSolutions[i].length() > maxStringLength) {
                maxStringLength = sSolutions[i].length();
            }
        }

        for (int i = solutions.length - 1; i >= 0; i--) {
            // index
            String indexFormat = "%" + ((int) Math.log10(solutions.length) + 1) + "d. ";
            summary.append(String.format(indexFormat, solutions.length - i));

            // time
            String timeFormat = "%" + maxStringLength + "s  ";
            summary.append(String.format(timeFormat, sSolutions[i]));

            // scramble
            summary.append(StringUtils.join(" ", solutions[i].getScramble().getSequence()));
            summary.append("\n");
        }

        this.textAreaSummary.setText(summary.toString());
        this.textAreaSummary.setCaretPosition(0);
    }
}

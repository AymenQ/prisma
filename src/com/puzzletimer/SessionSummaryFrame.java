package com.puzzletimer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.FullSolution;
import com.puzzletimer.models.Solution;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.SessionListener;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.BestAverage;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.Worst;
import com.puzzletimer.util.SolutionUtils;

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
                setTitle("Session Summary - " + currentCategory.description);
            }
        });
        categoryManager.notifyListeners();

        // summary
        sessionManager.addSessionListener(new SessionListener() {
            @Override
            public void solutionsUpdated(FullSolution[] solutions) {
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
        add(this.buttonOk, "width 100, right");
    }

    private void updateSummary(Category currentCategory, FullSolution[] fullSolutions) {
        StringBuilder summary = new StringBuilder();

        // categoryName
        summary.append(currentCategory.description);
        summary.append("\n");

        Solution[] solutions = new Solution[fullSolutions.length];
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = fullSolutions[i].getSolution();
        }

        if (fullSolutions.length >= 1) {
            // session interval
            Date start = fullSolutions[fullSolutions.length - 1].getSolution().timing.getStart();
            Date end = fullSolutions[0].getSolution().timing.getEnd();

            DateFormat dateTimeFormat =
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            DateFormat timeFormat =
                DateFormat.getTimeInstance(DateFormat.MEDIUM);

            summary.append(dateTimeFormat.format(start) + " - " + timeFormat.format(end));
            summary.append("\n");
            summary.append("\n");

            // mean
            Mean mean = new Mean(1, Integer.MAX_VALUE);
            mean.setSolutions(solutions);

            summary.append("Mean:               " + SolutionUtils.formatSeconds(mean.getValue()));
            summary.append("\n");

            // standard deviation
            StandardDeviation standardDeviation = new StandardDeviation(1, Integer.MAX_VALUE);
            standardDeviation.setSolutions(solutions);

            summary.append("Standard deviation: " + SolutionUtils.formatSeconds(standardDeviation.getValue()));
            summary.append("\n");

            // best
            Best best = new Best(1, Integer.MAX_VALUE);
            best.setSolutions(solutions);

            summary.append("Best Time:          " + SolutionUtils.formatSeconds(best.getValue()));
            summary.append("\n");

            // worst
            Worst worst = new Worst(1, Integer.MAX_VALUE);
            worst.setSolutions(solutions);

            summary.append("Worst Time:         " + SolutionUtils.formatSeconds(worst.getValue()));
            summary.append("\n");
            summary.append("\n");
        }

        // best average of 5
        if (fullSolutions.length >= 5) {
            BestAverage bestAverage = new BestAverage(5, Integer.MAX_VALUE);
            bestAverage.setSolutions(solutions);

            summary.append("Best Average of 5:  " + SolutionUtils.formatSeconds(bestAverage.getValue()));
            summary.append("\n");

            int indexBest = 0;
            int indexWorst = 0;
            long[] times = new long[5];
            for (int i = 0; i < 5; i++) {
                times[i] = SolutionUtils.realTime(solutions[bestAverage.getWindowPosition() + i]);

                if (times[i] < times[indexBest]) {
                    indexBest = i;
                }

                if (times[i] > times[indexWorst]) {
                    indexWorst = i;
                }
            }

            String sTimes = "  ";
            for (int i = 4; i >= 0; i--) {
                if (i == indexBest || i == indexWorst) {
                    sTimes += "(" + SolutionUtils.formatSeconds(times[i]) + ") ";
                } else {
                    sTimes += SolutionUtils.formatSeconds(times[i]) + " ";
                }
            }

            summary.append(sTimes);
            summary.append("\n");
            summary.append("\n");
        }

        // best average of 12
        if (fullSolutions.length >= 12) {
            BestAverage bestAverage = new BestAverage(12, Integer.MAX_VALUE);
            bestAverage.setSolutions(solutions);

            summary.append("Best Average of 12: " + SolutionUtils.formatSeconds(bestAverage.getValue()));
            summary.append("\n");

            int indexBest = 0;
            int indexWorst = 0;
            long[] times = new long[12];
            for (int i = 0; i < 12; i++) {
                times[i] = SolutionUtils.realTime(solutions[bestAverage.getWindowPosition() + i]);

                if (times[i] < times[indexBest]) {
                    indexBest = i;
                }

                if (times[i] > times[indexWorst]) {
                    indexWorst = i;
                }
            }

            String sTimes = "  ";
            for (int i = 11; i >= 0; i--) {
                if (i == indexBest || i == indexWorst) {
                    sTimes += "(" + SolutionUtils.formatSeconds(times[i]) + ") ";
                } else {
                    sTimes += SolutionUtils.formatSeconds(times[i]) + " ";
                }
            }

            summary.append(sTimes);
            summary.append("\n");
            summary.append("\n");
        }

        // solutions
        int indexBest = 0;
        int indexWorst = 0;
        String[] sSolutions = new String[fullSolutions.length];
        int longestSolutionString = 0;
        for (int i = 0; i < fullSolutions.length; i++) {
            long time = SolutionUtils.realTime(fullSolutions[i].getSolution());

            long bestTime = SolutionUtils.realTime(fullSolutions[indexBest].getSolution());
            if (time < bestTime) {
                indexBest = i;
            }

            long worstTime = SolutionUtils.realTime(fullSolutions[indexWorst].getSolution());
            if (time > worstTime) {
                indexWorst = i;
            }

            sSolutions[i] = SolutionUtils.formatSeconds(time);
            if (sSolutions[i].length() > longestSolutionString) {
                longestSolutionString = sSolutions[i].length();
            }
        }

        for (int i = fullSolutions.length - 1; i >= 0; i--) {
            // index
            String indexFormat = "%" + ((int) Math.log10(fullSolutions.length) + 1) + "d. ";
            summary.append(String.format(indexFormat, fullSolutions.length - i));

            // time
            String timeFormat = "%" + longestSolutionString + "s";
            if (fullSolutions.length >= 3 && (i == indexBest || i == indexWorst)) {
                timeFormat = "(" + timeFormat + ")  ";
            } else {
                timeFormat = " " + timeFormat + "   ";
            }
            summary.append(String.format(timeFormat, sSolutions[i]));

            // scramble
            StringBuilder sbScramble = new StringBuilder();
            for (String move : fullSolutions[i].getScramble().getSequence()) {
                sbScramble.append(move + " ");
            }
            summary.append(sbScramble.toString().trim());

            summary.append("\n");
        }

        this.textAreaSummary.setText(summary.toString());
    }
}

package com.puzzletimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.FullSolution;
import com.puzzletimer.models.Solution;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.SolutionListener;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.BestAverage;
import com.puzzletimer.statistics.BestMean;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.statistics.Worst;
import com.puzzletimer.util.SolutionUtils;

@SuppressWarnings("serial")
public class HistoryFrame extends JFrame {
    private HistogramPanel histogramPanel;
    private JLabel labelMean;
    private JLabel labelStandardDeviation;
    private JLabel labelBest;
    private JLabel labelWorst;
    private JLabel labelMeanOf3;
    private JLabel labelMeanOf10;
    private JLabel labelMeanOf100;
    private JLabel labelMeanOf1000;
    private JLabel labelAverageOf5;
    private JLabel labelAverageOf12;
    private JLabel labelAverageOf100;
    private JLabel labelAverageOf1000;
    private JLabel labelBestMeanOf3;
    private JLabel labelBestMeanOf10;
    private JLabel labelBestAverageOf5;
    private JLabel labelBestAverageOf12;
    private JTable table;
    private JButton buttonOk;

    public HistoryFrame(final CategoryManager categoryManager, final SolutionManager solutionManager) {
        super();

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(getMinimumSize());

        createComponents();

        // title
        categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                setTitle("History - " + currentCategory.description);
            }
        });
        categoryManager.notifyListeners();

        // statistics, table
        solutionManager.addSolutionListener(new SolutionListener() {
            @Override
            public void solutionsUpdated(FullSolution[] solutions) {
                updateHistogram(solutions);
                updateStatistics(solutions);
                updateTable(solutions);
            }
        });
        solutionManager.notifyListeners();

        // table selection
        this.table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    FullSolution[] solutions = solutionManager.getSolutions();
                    FullSolution[] selectedSolutions;

                    int[] rows = HistoryFrame.this.table.getSelectedRows();
                    if (rows.length <= 0) {
                        selectedSolutions = solutions;
                    } else {
                        selectedSolutions = new FullSolution[rows.length];
                        for (int i = 0; i < selectedSolutions.length; i++) {
                            selectedSolutions[i] = solutions[rows[i]];
                        }
                    }

                    updateHistogram(selectedSolutions);
                    updateStatistics(selectedSolutions);
                }
            });

        // close button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                HistoryFrame.this.setVisible(false);
            }
        });
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "",
                "[pref!][pref!]12[pref!][pref!]12[pref!][pref!]12[pref!][]16[pref!]"));

        // labelHistogram
        add(new JLabel("Histogram"), "wrap");

        // histogram
        this.histogramPanel = new HistogramPanel(new Solution[0], 17);
        add(this.histogramPanel, "growx, height 100, wrap");

        // labelGraph
        add(new JLabel("Graph"), "wrap");

        // Graph
        JPanel graph = new JPanel();
        graph.setBackground(Color.WHITE);
        add(graph, "growx, height 100, wrap");

        // labelStatistics
        add(new JLabel("Statistics"), "wrap");

        // panelStatistics
        JPanel panelStatistics = new JPanel(
            new MigLayout(
                "fill, insets 0 n 0 n",
                "[][pref!]32[][pref!]32[][pref!]32[][pref!]",
                ""));
        add(panelStatistics, "growx, wrap");

        // labelMean
        panelStatistics.add(new JLabel("Mean:"), "");
        this.labelMean = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMean, "");

        // labelStandardDeviation
        panelStatistics.add(new JLabel("Standard Deviation:"), "");
        this.labelStandardDeviation = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelStandardDeviation, "");

        // labelBest
        panelStatistics.add(new JLabel("Best:"), "");
        this.labelBest = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBest, "");

        // labelWorst
        panelStatistics.add(new JLabel("Worst:"), "");
        this.labelWorst = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelWorst, "wrap");

        // labelMeanOf3
        panelStatistics.add(new JLabel("Mean of 3:"), "");
        this.labelMeanOf3 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf3, "");

        // labelMeanOf10
        panelStatistics.add(new JLabel("Mean of 10:"), "");
        this.labelMeanOf10 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf10, "");

        // labelMeanOf100
        panelStatistics.add(new JLabel("Mean of 100:"), "");
        this.labelMeanOf100 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf100, "");

        // labelBestMeanOf1000
        panelStatistics.add(new JLabel("Mean of 1000:"), "");
        this.labelMeanOf1000 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf1000, "wrap");

        // labelAverageOf5
        panelStatistics.add(new JLabel("Average of 5:"), "");
        this.labelAverageOf5 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelAverageOf5, "");

        // labelAverageOf12
        panelStatistics.add(new JLabel("Average of 12:"), "");
        this.labelAverageOf12 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelAverageOf12, "");

        // labelAverageOf100
        panelStatistics.add(new JLabel("Average of 100:"), "");
        this.labelAverageOf100 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelAverageOf100, "");

        // labelAverageOf1000
        panelStatistics.add(new JLabel("Average of 1000:"), "");
        this.labelAverageOf1000 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelAverageOf1000, "wrap");

        // labelBestMeanOf3
        panelStatistics.add(new JLabel("Best mean of 3:"), "");
        this.labelBestMeanOf3 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestMeanOf3, "");

        // labelBestMeanOf10
        panelStatistics.add(new JLabel("Best mean of 10:"), "");
        this.labelBestMeanOf10 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestMeanOf10, "");

        // labelBestAverageOf5
        panelStatistics.add(new JLabel("Best average of 5:"), "");
        this.labelBestAverageOf5 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestAverageOf5, "");

        // labelBestAverageOf12
        panelStatistics.add(new JLabel("Best average of 12:"), "");
        this.labelBestAverageOf12 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestAverageOf12, "");

        // labelTimes
        JLabel labelTimes = new JLabel("Times");
        add(labelTimes, "wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        add(scrollPane, "grow, wrap");

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "width 100, right");
    }

    private void updateHistogram(FullSolution[] fullSolutions) {
        Solution[] solutions = new Solution[fullSolutions.length];
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = fullSolutions[i].getSolution();
        }

        this.histogramPanel.setSolutions(solutions);
    }

    private void updateStatistics(FullSolution[] solutions) {
        JLabel labels[] = {
            this.labelMean,
            this.labelStandardDeviation,
            this.labelBest,
            this.labelWorst,

            this.labelMeanOf3,
            this.labelMeanOf10,
            this.labelMeanOf100,
            this.labelMeanOf1000,

            this.labelAverageOf5,
            this.labelAverageOf12,
            this.labelAverageOf100,
            this.labelAverageOf1000,

            this.labelBestMeanOf3,
            this.labelBestMeanOf10,
            this.labelBestAverageOf5,
            this.labelBestAverageOf12,
        };

        StatisticalMeasure[] measures = {
            new Mean(1, Integer.MAX_VALUE),
            new StandardDeviation(1, Integer.MAX_VALUE),
            new Best(1, Integer.MAX_VALUE),
            new Worst(1, Integer.MAX_VALUE),

            new Mean(3, 3),
            new Mean(10, 10),
            new Mean(100, 100),
            new Mean(1000, 1000),

            new Average(5, 5),
            new Average(12, 12),
            new Average(100, 100),
            new Average(1000, 1000),

            new BestMean(3, Integer.MAX_VALUE),
            new BestMean(10, Integer.MAX_VALUE),
            new BestAverage(5, Integer.MAX_VALUE),
            new BestAverage(12, Integer.MAX_VALUE),
        };

        for (int i = 0; i < labels.length; i++) {
            if (solutions.length >= measures[i].getMinimumWindowSize()) {
                int size = Math.min(solutions.length, measures[i].getMaximumWindowSize());

                Solution[] window = new Solution[size];
                for (int j = 0; j < size; j++) {
                    window[j] = solutions[solutions.length - size + j].getSolution();
                }

                labels[i].setText(SolutionUtils.formatMinutes(measures[i].calculate(window)));
            } else {
                labels[i].setText("XX:XX.XX");
            }
        }
    }

    private void updateTable(FullSolution[] solutions) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String column : new String[] { "Start", "Time", "Penalty", "Scramble" }) {
            tableModel.addColumn(column);
        }

        for (FullSolution completeSolution : solutions) {
            // start
            DateFormat dateFormat = new SimpleDateFormat();
            String sStart = dateFormat.format(completeSolution.getSolution().timing.getStart());

            // time
            String sTime = SolutionUtils.formatMinutes(completeSolution.getSolution().timing.getElapsedTime());

            // scramble
            StringBuilder stringBuilder = new StringBuilder();
            for (String move : completeSolution.getScramble().getSequence()) {
                stringBuilder.append(move + " ");
            }
            String sScramble = stringBuilder.toString().trim();

            tableModel.addRow(new Object[] {
                sStart,
                sTime,
                completeSolution.getSolution().penalty,
                sScramble,
            });
        }

        this.table.setModel(tableModel);
    }
}

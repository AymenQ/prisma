package com.puzzletimer.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.parsers.ScrambleParser;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ScrambleManager;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.state.SolutionListener;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.BestAverage;
import com.puzzletimer.statistics.BestMean;
import com.puzzletimer.statistics.InterquartileMean;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.Percentile;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.statistics.Worst;
import com.puzzletimer.util.SolutionUtils;
import com.puzzletimer.util.StringUtils;

@SuppressWarnings("serial")
public class HistoryFrame extends JFrame {
    private static class SolutionImporterDialog extends JDialog {
        public static class SolutionImporterListener {
            public void solutionsImported(Solution[] solutions) {
            }
        }

        private JTextArea textAreaContents;
        private JButton buttonOk;
        private JButton buttonCancel;

        public SolutionImporterDialog(
                JFrame owner,
                boolean modal,
                final UUID categoryId,
                final String scramblerId,
                final ScrambleParser scrambleParser,
                final SolutionImporterListener listener) {
            super(owner, modal);

            setTitle("Solution Importer");
            setMinimumSize(new Dimension(640, 480));
            setPreferredSize(getMinimumSize());

            createComponents();

            // ok button
            this.buttonOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    String contents =
                        SolutionImporterDialog.this.textAreaContents.getText();

                    Date start = new Date();
                    ArrayList<Solution> solutions = new ArrayList<Solution>();
                    for (String line : contents.split("\n")) {
                        line = line.trim();

                        // ignore blank lines and comments
                        if (line.length() == 0 || line.startsWith("#")) {
                            continue;
                        }

                        // separate time from scramble
                        String[] parts = line.split("\\s+", 2);

                        // time
                        long time = SolutionUtils.parseTime(parts[0]);
                        Timing timing =
                            new Timing(
                                start,
                                new Date(start.getTime() + time));

                        // scramble
                        Scramble scramble = new Scramble(scramblerId, new String[0]);
                        if (parts.length > 1) {
                            scramble = new Scramble(
                                scramblerId,
                                scrambleParser.parse(parts[1]));
                        }

                        solutions.add(
                            new Solution(
                                UUID.randomUUID(),
                                categoryId,
                                scramble,
                                timing,
                                ""));

                        start = new Date(start.getTime() + time);
                    }

                    Solution[] solutionsArray = new Solution[solutions.size()];
                    solutions.toArray(solutionsArray);

                    listener.solutionsImported(solutionsArray);

                    SolutionImporterDialog.this.dispose();
                }
            });

            // cancel button
            this.buttonCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    SolutionImporterDialog.this.dispose();
                }
            });

            // esc key closes window
            this.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        SolutionImporterDialog.this.dispose();
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
                    "[pref!][fill]16[pref!]"));

            // labelSolutions
            add(new JLabel("Solutions"), "wrap");

            // textAreaContents
            this.textAreaContents = new JTextArea(
                "# one solution per line\n" +
                "# format: time [scramble]");
            JScrollPane scrollPane = new JScrollPane(this.textAreaContents);
            add(scrollPane, "growx, wrap");

            // buttonOk
            this.buttonOk = new JButton("OK");
            add(this.buttonOk, "right, width 100, span 2, split");

            // buttonCancel
            this.buttonCancel = new JButton("Cancel");
            add(this.buttonCancel, "width 100");
        }
    }

    private HistogramPanel histogramPanel;
    private GraphPanel graphPanel;

    private JLabel labelMean;
    private JLabel labelBest;
    private JLabel labelMeanOf3;
    private JLabel labelBestMeanOf3;
    private JLabel labelInterquartileMean;
    private JLabel labelLowerQuartile;
    private JLabel labelMeanOf10;
    private JLabel labelBestMeanOf10;
    private JLabel labelStandardDeviation;
    private JLabel labelMedian;
    private JLabel labelMeanOf100;
    private JLabel labelBestMeanOf100;
    private JLabel labelUpperQuartile;
    private JLabel labelAverageOf5;
    private JLabel labelBestAverageOf5;
    private JLabel labelWorst;
    private JLabel labelAverageOf12;
    private JLabel labelBestAverageOf12;
    private JTable table;
    private JButton buttonAddSolutions;
    private JButton buttonEdit;
    private JButton buttonRemove;
    private JButton buttonSelectSession;
    private JButton buttonSelectNone;
    private JButton buttonOk;

    public HistoryFrame(
            final ScramblerProvider scramblerProvider,
            final ScrambleParserProvider scrambleParserProvider,
            final CategoryManager categoryManager,
            final ScrambleManager scrambleManager,
            final SolutionManager solutionManager,
            final SessionManager sessionManager) {
        super();

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(getMinimumSize());

        createComponents();

        // title
        categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                setTitle("History - " + currentCategory.getDescription());
            }
        });
        categoryManager.notifyListeners();

        // statistics, table
        solutionManager.addSolutionListener(new SolutionListener() {
            @Override
            public void solutionsUpdated(Solution[] solutions) {
                int[] selectedRows = new int[solutions.length];
                for (int i = 0; i < selectedRows.length; i++) {
                    selectedRows[i] = i;
                }

                HistoryFrame.this.histogramPanel.setSolutions(solutions);
                HistoryFrame.this.graphPanel.setSolutions(solutions);
                updateStatistics(solutions, selectedRows);
                updateTable(solutions);
            }
        });
        solutionManager.notifyListeners();

        // table selection
        this.table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    Solution[] solutions = solutionManager.getSolutions();
                    Solution[] selectedSolutions;

                    int[] selectedRows = HistoryFrame.this.table.getSelectedRows();
                    if (selectedRows.length <= 0) {
                        selectedRows = new int[HistoryFrame.this.table.getRowCount()];
                        for (int i = 0; i < selectedRows.length; i++) {
                            selectedRows[i] = i;
                        }

                        selectedSolutions = solutions;
                    } else {
                        selectedSolutions = new Solution[selectedRows.length];
                        for (int i = 0; i < selectedSolutions.length; i++) {
                            selectedSolutions[i] = solutions[selectedRows[i]];
                        }
                    }

                    HistoryFrame.this.histogramPanel.setSolutions(selectedSolutions);
                    HistoryFrame.this.graphPanel.setSolutions(selectedSolutions);
                    updateStatistics(selectedSolutions, selectedRows);

                    HistoryFrame.this.buttonEdit.setEnabled(
                        HistoryFrame.this.table.getSelectedRowCount() == 1);
                    HistoryFrame.this.buttonRemove.setEnabled(
                        HistoryFrame.this.table.getSelectedRowCount() > 0);
                }
            });

        // add solutions button
        this.buttonAddSolutions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SolutionImporterDialog.SolutionImporterListener listener =
                    new SolutionImporterDialog.SolutionImporterListener() {
                        @Override
                        public void solutionsImported(Solution[] solutions) {
                            solutionManager.addSolutions(solutions);
                        }
                    };

                Category currentCategory = categoryManager.getCurrentCategory();
                Scrambler currentScrambler = scramblerProvider.get(currentCategory.getScramblerId());
                String puzzleId = currentScrambler.getScramblerInfo().getPuzzleId();
                ScrambleParser scrambleParser = scrambleParserProvider.get(puzzleId);

                SolutionImporterDialog solutionEditingDialog =
                    new SolutionImporterDialog(
                        HistoryFrame.this,
                        true,
                        currentCategory.getCategoryId(),
                        currentCategory.getScramblerId(),
                        scrambleParser,
                        listener);
                solutionEditingDialog.setLocationRelativeTo(null);
                solutionEditingDialog.setVisible(true);
            }
        });

        // edit button
        this.buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Solution[] solutions = solutionManager.getSolutions();
                Solution solution = solutions[HistoryFrame.this.table.getSelectedRow()];

                SolutionEditingDialog.SolutionEditingDialogListener listener =
                    new SolutionEditingDialog.SolutionEditingDialogListener() {
                        @Override
                        public void solutionEdited(Solution solution) {
                            solutionManager.updateSolution(solution);
                        }
                    };

                SolutionEditingDialog solutionEditingDialog =
                    new SolutionEditingDialog(
                        HistoryFrame.this,
                        true,
                        solution,
                        listener);
                solutionEditingDialog.setLocationRelativeTo(null);
                solutionEditingDialog.setVisible(true);
            }
        });

        // remove button
        this.buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (HistoryFrame.this.table.getSelectedRows().length > 5) {
                    int result = JOptionPane.showConfirmDialog(
                        HistoryFrame.this,
                        "The selected solutions will be removed. Proceed?",
                        "Remove Solutions",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                Solution[] solutions = solutionManager.getSolutions();

                int[] selectedRows = HistoryFrame.this.table.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    solutionManager.removeSolution(solutions[selectedRows[i]]);
                }

                // request focus
                HistoryFrame.this.buttonRemove.requestFocusInWindow();
            }
        });

        // select session button
        this.buttonSelectSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (HistoryFrame.this.table.getRowCount() > 0) {
                    HistoryFrame.this.table.removeRowSelectionInterval(
                        0,
                        HistoryFrame.this.table.getRowCount() - 1);
                }

                Solution[] solutions = solutionManager.getSolutions();
                Solution[] sessionSolutions = sessionManager.getSolutions();

                for (int i = 0, j = 0; i < solutions.length && j < sessionSolutions.length; i++) {
                    if (solutions[i].getSolutionId().equals(sessionSolutions[j].getSolutionId())) {
                        HistoryFrame.this.table.addRowSelectionInterval(i, i);
                        j++;
                    }
                }
            }
        });

        // select none button
        this.buttonSelectNone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (HistoryFrame.this.table.getRowCount() > 0) {
                    HistoryFrame.this.table.removeRowSelectionInterval(
                        0,
                        HistoryFrame.this.table.getRowCount() - 1);
                }
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

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    HistoryFrame.this.setVisible(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "[][pref!]",
                "[pref!][pref!]12[pref!][pref!]12[pref!][pref!]12[pref!][]16[pref!]"));

        // labelHistogram
        add(new JLabel("Histogram"), "span, wrap");

        // histogram
        this.histogramPanel = new HistogramPanel(new Solution[0], 17);
        add(this.histogramPanel, "growx, height 90, span, wrap");

        // labelGraph
        add(new JLabel("Graph"), "span, wrap");

        // Graph
        this.graphPanel = new GraphPanel(new Solution[0]);
        add(this.graphPanel, "growx, height 90, span, wrap");

        // labelStatistics
        add(new JLabel("Statistics"), "span, wrap");

        // panelStatistics
        JPanel panelStatistics = new JPanel(
            new MigLayout(
                "fill, insets 0 n 0 n",
                "[][pref!]32[][pref!]32[][pref!]32[][pref!]",
                "[pref!]1[pref!]1[pref!]1[pref!]1[pref!]"));
        add(panelStatistics, "growx, span, wrap");

        // labelMean
        panelStatistics.add(new JLabel("Mean:"), "");
        this.labelMean = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMean, "");

        // labelBest
        panelStatistics.add(new JLabel("Best:"), "");
        this.labelBest = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBest, "");

        // labelMeanOf3
        panelStatistics.add(new JLabel("Mean of 3:"), "");
        this.labelMeanOf3 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf3, "");

        // labelBestMeanOf3
        panelStatistics.add(new JLabel("Best mean of 3:"), "");
        this.labelBestMeanOf3 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestMeanOf3, "wrap");

        // labelInterquartileMean
        panelStatistics.add(new JLabel("Interquartile mean:"), "");
        this.labelInterquartileMean = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelInterquartileMean, "");

        // labelLowerQuartile
        panelStatistics.add(new JLabel("Lower quartile:"), "");
        this.labelLowerQuartile = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelLowerQuartile, "");

        // labelMeanOf10
        panelStatistics.add(new JLabel("Mean of 10:"), "");
        this.labelMeanOf10 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf10, "");

        // labelBestMeanOf10
        panelStatistics.add(new JLabel("Best mean of 10:"), "");
        this.labelBestMeanOf10 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestMeanOf10, "wrap");

        // labelStandardDeviation
        panelStatistics.add(new JLabel("Standard deviation:"), "");
        this.labelStandardDeviation = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelStandardDeviation, "");

        // labelMedian
        panelStatistics.add(new JLabel("Median:"), "");
        this.labelMedian = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMedian, "");

        // labelMeanOf100
        panelStatistics.add(new JLabel("Mean of 100:"), "");
        this.labelMeanOf100 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelMeanOf100, "");

        // labelBestMeanOf100
        panelStatistics.add(new JLabel("Best mean of 100:"), "");
        this.labelBestMeanOf100 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestMeanOf100, "wrap");

        // labelUpperQuartile
        panelStatistics.add(new JLabel("Upper quartile:"), "skip 2");
        this.labelUpperQuartile = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelUpperQuartile, "");

        // labelAverageOf5
        panelStatistics.add(new JLabel("Average of 5:"), "");
        this.labelAverageOf5 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelAverageOf5, "");

        // labelBestAverageOf5
        panelStatistics.add(new JLabel("Best average of 5:"), "");
        this.labelBestAverageOf5 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestAverageOf5, "wrap");

        // labelWorst
        panelStatistics.add(new JLabel("Worst:"), "skip 2");
        this.labelWorst = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelWorst, "");

        // labelAverageOf12
        panelStatistics.add(new JLabel("Average of 12:"), "");
        this.labelAverageOf12 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelAverageOf12, "");

        // labelBestAverageOf12
        panelStatistics.add(new JLabel("Best average of 12:"), "");
        this.labelBestAverageOf12 = new JLabel("XX:XX.XX");
        panelStatistics.add(this.labelBestAverageOf12, "");

        // labelSolutions
        JLabel labelTimes = new JLabel("Solutions");
        add(labelTimes, "span, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        add(scrollPane, "grow");

        // buttonAddSolutions
        this.buttonAddSolutions = new JButton("Add solutions...");
        add(this.buttonAddSolutions, "growx, top, split 5, flowy");

        // buttonEdit
        this.buttonEdit = new JButton("Edit...");
        this.buttonEdit.setEnabled(false);
        add(this.buttonEdit, "growx, top");

        // buttonRemove
        this.buttonRemove = new JButton("Remove");
        this.buttonRemove.setEnabled(false);
        add(this.buttonRemove, "growx, top");

        // buttonSelectSession
        this.buttonSelectSession = new JButton("Select session");
        add(this.buttonSelectSession, "growx, top, gaptop 16");

        // buttonSelectNone
        this.buttonSelectNone = new JButton("Select none");
        add(this.buttonSelectNone, "growx, top, wrap");

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "tag ok, span");
    }

    private void updateStatistics(Solution[] solutions, final int[] selectedRows) {
        JLabel labels[] = {
            this.labelMean,
            this.labelBest,
            this.labelMeanOf3,
            this.labelBestMeanOf3,
            this.labelInterquartileMean,
            this.labelLowerQuartile,
            this.labelMeanOf10,
            this.labelBestMeanOf10,
            this.labelStandardDeviation,
            this.labelMedian,
            this.labelMeanOf100,
            this.labelBestMeanOf100,
            this.labelUpperQuartile,
            this.labelAverageOf5,
            this.labelBestAverageOf5,
            this.labelWorst,
            this.labelAverageOf12,
            this.labelBestAverageOf12,
        };

        StatisticalMeasure[] measures = {
            new Mean(1, Integer.MAX_VALUE),
            new Best(1, Integer.MAX_VALUE),
            new Mean(3, 3),
            new BestMean(3, Integer.MAX_VALUE),
            new InterquartileMean(3, Integer.MAX_VALUE),
            new Percentile(1, Integer.MAX_VALUE, 0.25),
            new Mean(10, 10),
            new BestMean(10, Integer.MAX_VALUE),
            new StandardDeviation(1, Integer.MAX_VALUE),
            new Percentile(1, Integer.MAX_VALUE, 0.5),
            new Mean(100, 100),
            new BestMean(100, Integer.MAX_VALUE),
            new Percentile(1, Integer.MAX_VALUE, 0.75),
            new Average(5, 5),
            new BestAverage(5, Integer.MAX_VALUE),
            new Worst(1, Integer.MAX_VALUE),
            new Average(12, 12),
            new BestAverage(12, Integer.MAX_VALUE),
        };

        boolean[] clickable = {
            false,
            true,
            true,
            true,
            false,
            false,
            true,
            true,
            false,
            false,
            true,
            true,
            false,
            true,
            true,
            true,
            true,
            true,
        };

        for (int i = 0; i < labels.length; i++) {
            if (solutions.length >= measures[i].getMinimumWindowSize()) {
                int size = Math.min(solutions.length, measures[i].getMaximumWindowSize());

                Solution[] window = new Solution[size];
                for (int j = 0; j < size; j++) {
                    window[j] = solutions[j];
                }

                measures[i].setSolutions(window);
                labels[i].setText(SolutionUtils.formatMinutes(measures[i].getValue()));
            } else {
                labels[i].setText("XX:XX.XX");
            }

            if (clickable[i]) {
                MouseListener[] mouseListeners = labels[i].getMouseListeners();
                for (MouseListener mouseListener : mouseListeners) {
                    labels[i].removeMouseListener(mouseListener);
                }

                labels[i].setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                if (solutions.length >= measures[i].getMinimumWindowSize()) {
                    labels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

                    final int windowSize = measures[i].getMinimumWindowSize();
                    final int windowPosition = measures[i].getWindowPosition();

                    labels[i].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (HistoryFrame.this.table.getRowCount() > 0) {
                                HistoryFrame.this.table.removeRowSelectionInterval(
                                    0,
                                    HistoryFrame.this.table.getRowCount() - 1);
                            }

                            for (int i = 0; i < windowSize; i++) {
                                HistoryFrame.this.table.addRowSelectionInterval(
                                    selectedRows[windowPosition + i],
                                    selectedRows[windowPosition + i]);
                            }
                        }
                    });
                }
            }
        }
    }

    private void updateTable(Solution[] solutions) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String column : new String[] { "#", "Start", "Time", "Penalty", "Scramble" }) {
            tableModel.addColumn(column);
        }

        this.table.setModel(tableModel);

        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        int[] columnsWidth = { 100, 400, 200, 200, 1000 };
        for (int i = 0; i < columnsWidth.length; i++) {
            TableColumn indexColumn = this.table.getColumnModel().getColumn(i);
            indexColumn.setPreferredWidth(columnsWidth[i]);
        }

        for (int i = 0; i < solutions.length; i++) {
            // start
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            String sStart = dateFormat.format(solutions[i].getTiming().getStart());

            // time
            String sTime = SolutionUtils.formatMinutes(solutions[i].getTiming().getElapsedTime());

            tableModel.addRow(new Object[] {
                solutions.length - i,
                sStart,
                sTime,
                solutions[i].getPenalty(),
                StringUtils.join(" ", solutions[i].getScramble().getSequence()),
            });
        }
    }
}

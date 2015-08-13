package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.puzzletimer.models.table.CustomTableModel;
import com.puzzletimer.models.table.SortButtonRenderer;
import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.parsers.ScrambleParser;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ConfigurationManager;
import com.puzzletimer.state.ScrambleManager;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.state.TimerManager;
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

            setTitle(_("solution_importer.solution_importer"));
            setMinimumSize(new Dimension(640, 480));

            createComponents();
            pack();

            // ok button
            this.buttonOk.addActionListener(event -> {
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
                            "",
                            ""));

                    start = new Date(start.getTime() + time);
                }

                Solution[] solutionsArray = new Solution[solutions.size()];
                solutions.toArray(solutionsArray);

                listener.solutionsImported(solutionsArray);

                SolutionImporterDialog.this.dispose();
            });

            // cancel button
            this.buttonCancel.addActionListener(event -> SolutionImporterDialog.this.dispose());

            // esc key closes window
            this.getRootPane().registerKeyboardAction(
                    arg0 -> SolutionImporterDialog.this.dispose(),
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
            add(new JLabel(_("solution_importer.solutions")), "wrap");

            // textAreaContents
            this.textAreaContents = new JTextArea(_("solution_importer.default_contents"));
            JScrollPane scrollPane = new JScrollPane(this.textAreaContents);
            scrollPane.setPreferredSize(new Dimension(0, 0));
            add(scrollPane, "growx, wrap");

            // buttonOk
            this.buttonOk = new JButton(_("solution_importer.ok"));
            add(this.buttonOk, "right, width 100, span 2, split");

            // buttonCancel
            this.buttonCancel = new JButton(_("solution_importer.cancel"));
            add(this.buttonCancel, "width 100");
        }
    }

    private HistogramPanel histogramPanel;
    private GraphPanel graphPanel;

    private JLabel labelNumberOfSolutions;
    private JLabel labelMean;
    private JLabel labelBest;
    private JLabel labelMeanOf3;
    private JLabel labelBestMeanOf3;
    private JLabel labelAverage;
    private JLabel labelLowerQuartile;
    private JLabel labelMeanOf10;
    private JLabel labelBestMeanOf10;
    private JLabel labelInterquartileMean;
    private JLabel labelMedian;
    private JLabel labelMeanOf100;
    private JLabel labelBestMeanOf100;
    private JLabel labelUpperQuartile;
    private JLabel labelAverageOf5;
    private JLabel labelBestAverageOf5;
    private JLabel labelStandardDeviation;
    private JLabel labelWorst;
    private JLabel labelAverageOf12;
    private JLabel labelBestAverageOf12;
    private JLabel labelAverageOf50;
    private JLabel labelBestAverageOf50;
    private JTable table;
    private JButton buttonAddSolutions;
    private JButton buttonEdit;
    private JButton buttonRemove;
    private JButton buttonSelectSession;
    private JButton buttonSelectNone;
    private JButton buttonOk;
    private String nullTime;
    private ConfigurationManager configurationManager;

    public HistoryFrame(
            final ScramblerProvider scramblerProvider,
            final ScrambleParserProvider scrambleParserProvider,
            final CategoryManager categoryManager,
            final ScrambleManager scrambleManager,
            final SolutionManager solutionManager,
            final SessionManager sessionManager,
            final TimerManager timerManager,
            ConfigurationManager configurationManager) {
        super();

        this.configurationManager = configurationManager;
        
        this.nullTime = "XX:XX.XX";

        setMinimumSize(new Dimension(800, 600));

        createComponents();
        pack();

        // title
        categoryManager.addListener(new CategoryManager.Listener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                setTitle(
                    String.format(
                        _("history.history_category"),
                        currentCategory.getDescription()));
            }
        });
        categoryManager.notifyListeners();
        
        timerManager.addListener(new TimerManager.Listener() {
        	@Override
        	public void precisionChanged(String timerPrecisionId) {
    			Solution[] solutions = solutionManager.getSolutions();
                int[] selectedRows = new int[solutions.length];
                for (int i = 0; i < selectedRows.length; i++) {
                    selectedRows[i] = i;
                }
        		if(timerPrecisionId.equals("CENTISECONDS")) {
        			HistoryFrame.this.nullTime = "XX:XX.XX";
        		} else if(timerPrecisionId.equals("MILLISECONDS")) {
        			HistoryFrame.this.nullTime = "XX:XX.XXX";
        		}
    			HistoryFrame.this.updateStatistics(solutions, selectedRows);
    			HistoryFrame.this.updateTable(solutions);
        	}
        });

        // statistics, table
        solutionManager.addListener(new SolutionManager.Listener() {
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
                event -> {
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
                });

        // add solutions button
        this.buttonAddSolutions.addActionListener(e -> {
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
        });

        // edit button
        this.buttonEdit.addActionListener(e -> {
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
                    listener,
                    HistoryFrame.this.configurationManager);
            solutionEditingDialog.setLocationRelativeTo(null);
            solutionEditingDialog.setVisible(true);
        });

        // remove button
        this.buttonRemove.addActionListener(e -> {
            if (HistoryFrame.this.table.getSelectedRows().length > 5) {
                int result = JOptionPane.showConfirmDialog(
                    HistoryFrame.this,
                    _("history.solution_removal_confirmation_message"),
                    _("history.remove_solutions"),
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
        });

        // select session button
        this.buttonSelectSession.addActionListener(e -> {
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
                    HistoryFrame.this.table.scrollRectToVisible(HistoryFrame.this.table.getCellRect(i, 0, true));
                    j++;
                }
            }
        });

        // select none button
        this.buttonSelectNone.addActionListener(e -> {
            if (HistoryFrame.this.table.getRowCount() > 0) {
                HistoryFrame.this.table.removeRowSelectionInterval(
                    0,
                    HistoryFrame.this.table.getRowCount() - 1);
            }
        });

        // close button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(arg0 -> HistoryFrame.this.setVisible(false));

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
                arg0 -> HistoryFrame.this.setVisible(false),
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
        add(new JLabel(_("history.histogram")), "span, wrap");

        // histogram
        this.histogramPanel = new HistogramPanel(new Solution[0], 17, this.configurationManager);
        add(this.histogramPanel, "growx, height 90, span, wrap");

        // labelGraph
        add(new JLabel(_("history.graph")), "span, wrap");

        // Graph
        this.graphPanel = new GraphPanel(new Solution[0], this.configurationManager);
        add(this.graphPanel, "growx, height 90, span, wrap");

        // labelStatistics
        add(new JLabel(_("history.statistics")), "span, wrap");

        // panelStatistics
        JPanel panelStatistics = new JPanel(
            new MigLayout(
                "fill, insets 0 n 0 n",
                "[][pref!]32[][pref!]32[][pref!]32[][pref!]",
                "[pref!]1[pref!]1[pref!]1[pref!]1[pref!]1[pref!]"));
        add(panelStatistics, "growx, span, wrap");

        // labelNumberOfSolutions
        panelStatistics.add(new JLabel(_("history.number_of_solutions")), "");
        this.labelNumberOfSolutions = new JLabel("");
        panelStatistics.add(this.labelNumberOfSolutions, "right");

        // labelBest
        panelStatistics.add(new JLabel(_("history.best")), "");
        this.labelBest = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBest, "right");

        // labelMeanOf3
        panelStatistics.add(new JLabel(_("history.mean_of_3")), "");
        this.labelMeanOf3 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelMeanOf3, "right");

        // labelBestMeanOf3
        panelStatistics.add(new JLabel(_("history.best_mean_of_3")), "");
        this.labelBestMeanOf3 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBestMeanOf3, "right, wrap");

        // labelMean
        panelStatistics.add(new JLabel(_("history.mean")), "");
        this.labelMean = new JLabel(this.nullTime);
        panelStatistics.add(this.labelMean, "right");

        // labelLowerQuartile
        panelStatistics.add(new JLabel(_("history.lower_quartile")), "");
        this.labelLowerQuartile = new JLabel(this.nullTime);
        panelStatistics.add(this.labelLowerQuartile, "right");

        // labelMeanOf10
        panelStatistics.add(new JLabel(_("history.mean_of_10")), "");
        this.labelMeanOf10 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelMeanOf10, "right");

        // labelBestMeanOf10
        panelStatistics.add(new JLabel(_("history.best_mean_of_10")), "");
        this.labelBestMeanOf10 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBestMeanOf10, "right, wrap");

        // labelAverage
        panelStatistics.add(new JLabel(_("history.average")), "");
        this.labelAverage = new JLabel(this.nullTime);
        panelStatistics.add(this.labelAverage, "right");

        // labelMedian
        panelStatistics.add(new JLabel(_("history.median")), "");
        this.labelMedian = new JLabel(this.nullTime);
        panelStatistics.add(this.labelMedian, "right");

        // labelMeanOf100
        panelStatistics.add(new JLabel(_("history.mean_of_100")), "");
        this.labelMeanOf100 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelMeanOf100, "right");

        // labelBestMeanOf100
        panelStatistics.add(new JLabel(_("history.best_mean_of_100")), "");
        this.labelBestMeanOf100 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBestMeanOf100, "right, wrap");

        // labelInterquartileMean
        panelStatistics.add(new JLabel(_("history.interquartile_mean")), "");
        this.labelInterquartileMean = new JLabel(this.nullTime);
        panelStatistics.add(this.labelInterquartileMean, "right");

        // labelUpperQuartile
        panelStatistics.add(new JLabel(_("history.upper_quartile")), "");
        this.labelUpperQuartile = new JLabel(this.nullTime);
        panelStatistics.add(this.labelUpperQuartile, "right");

        // labelAverageOf5
        panelStatistics.add(new JLabel(_("history.average_of_5")), "");
        this.labelAverageOf5 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelAverageOf5, "right");

        // labelBestAverageOf5
        panelStatistics.add(new JLabel(_("history.best_average_of_5")), "");
        this.labelBestAverageOf5 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBestAverageOf5, "right, wrap");

        // labelStandardDeviation
        panelStatistics.add(new JLabel(_("history.standard_deviation")), "");
        this.labelStandardDeviation = new JLabel(this.nullTime);
        panelStatistics.add(this.labelStandardDeviation, "right");

        // labelWorst
        panelStatistics.add(new JLabel(_("history.worst")), "");
        this.labelWorst = new JLabel(this.nullTime);
        panelStatistics.add(this.labelWorst, "right");

        // labelAverageOf12
        panelStatistics.add(new JLabel(_("history.average_of_12")), "");
        this.labelAverageOf12 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelAverageOf12, "right");

        // labelBestAverageOf12
        panelStatistics.add(new JLabel(_("history.best_average_of_12")), "");
        this.labelBestAverageOf12 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBestAverageOf12, "right, wrap");

        // labelAverageOf50
        panelStatistics.add(new JLabel(_("history.average_of_50")), "skip 4");
        this.labelAverageOf50 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelAverageOf50, "right");

        // labelBestAverageOf50
        panelStatistics.add(new JLabel(_("history.best_average_of_50")), "");
        this.labelBestAverageOf50 = new JLabel(this.nullTime);
        panelStatistics.add(this.labelBestAverageOf50, "right");

        // labelSolutions
        JLabel labelTimes = new JLabel(_("history.solutions"));
        add(labelTimes, "span, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane, "grow");

        // buttonAddSolutions
        this.buttonAddSolutions = new JButton(_("history.add_solutions"));
        add(this.buttonAddSolutions, "growx, top, split 5, flowy");

        // buttonEdit
        this.buttonEdit = new JButton(_("history.edit"));
        this.buttonEdit.setEnabled(false);
        add(this.buttonEdit, "growx, top");

        // buttonRemove
        this.buttonRemove = new JButton(_("history.remove"));
        this.buttonRemove.setEnabled(false);
        add(this.buttonRemove, "growx, top");

        // buttonSelectSession
        this.buttonSelectSession = new JButton(_("history.select_session"));
        add(this.buttonSelectSession, "growx, top, gaptop 16");

        // buttonSelectNone
        this.buttonSelectNone = new JButton(_("history.select_none"));
        add(this.buttonSelectNone, "growx, top, wrap");

        // buttonOk
        this.buttonOk = new JButton(_("history.ok"));
        add(this.buttonOk, "tag ok, span");
    }

    private void updateStatistics(Solution[] solutions, final int[] selectedRows) {
        this.labelNumberOfSolutions.setText(Integer.toString(solutions.length));

        JLabel labels[] = {
            this.labelBest,
            this.labelMeanOf3,
            this.labelBestMeanOf3,

            this.labelMean,
            this.labelLowerQuartile,
            this.labelMeanOf10,
            this.labelBestMeanOf10,

            this.labelAverage,
            this.labelMedian,
            this.labelMeanOf100,
            this.labelBestMeanOf100,

            this.labelInterquartileMean,
            this.labelUpperQuartile,
            this.labelAverageOf5,
            this.labelBestAverageOf5,

            this.labelStandardDeviation,
            this.labelWorst,
            this.labelAverageOf12,
            this.labelBestAverageOf12,
            this.labelAverageOf50,
            this.labelBestAverageOf50,
        };

        StatisticalMeasure[] measures = {
            new Best(1, Integer.MAX_VALUE),
            new Mean(3, 3),
            new BestMean(3, Integer.MAX_VALUE),

            new Mean(1, Integer.MAX_VALUE),
            new Percentile(1, Integer.MAX_VALUE, 0.25),
            new Mean(10, 10),
            new BestMean(10, Integer.MAX_VALUE),

            new Average(3, Integer.MAX_VALUE),
            new Percentile(1, Integer.MAX_VALUE, 0.5),
            new Mean(100, 100),
            new BestMean(100, Integer.MAX_VALUE),

            new InterquartileMean(3, Integer.MAX_VALUE),
            new Percentile(1, Integer.MAX_VALUE, 0.75),
            new Average(5, 5),
            new BestAverage(5, Integer.MAX_VALUE),

            new StandardDeviation(1, Integer.MAX_VALUE),
            new Worst(1, Integer.MAX_VALUE),
            new Average(12, 12),
            new BestAverage(12, Integer.MAX_VALUE),
            new Average(50, 50),
            new BestAverage(50, Integer.MAX_VALUE),
        };

        boolean[] clickable = {
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
                System.arraycopy(solutions, 0, window, 0, size);

                measures[i].setSolutions(window, this.configurationManager.getConfiguration("TIMER-PRECISION").equals("CENTISECONDS"));
                labels[i].setText(SolutionUtils.formatMinutes(measures[i].getValue(), this.configurationManager.getConfiguration("TIMER-PRECISION"), measures[i].getRound()));
            } else {
                labels[i].setText(this.nullTime);
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
                                HistoryFrame.this.table.scrollRectToVisible(HistoryFrame.this.table.getCellRect(selectedRows[windowPosition + i], 0, true));
                            }
                        }
                    });
                }
            }
        }
    }

    private void updateTable(Solution[] solutions) {
        CustomTableModel tableModel = new CustomTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn(_("history.#"));
        tableModel.addColumn(_("history.start"));
        tableModel.addColumn(_("history.time"));
        tableModel.addColumn(_("history.penalty"));
        tableModel.addColumn(_("history.comment"));
        tableModel.addColumn(_("history.scramble"));

        this.table.setModel(tableModel);

        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        int[] columnsWidth = { 100, 400, 200, 200, 300, 1000 };
        for (int i = 0; i < columnsWidth.length; i++) {
            TableColumn indexColumn = this.table.getColumnModel().getColumn(i);
            indexColumn.setPreferredWidth(columnsWidth[i]);
        }

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        for (int i = 0; i < solutions.length; i++) {
            // start
            String sStart = dateFormat.format(solutions[i].getTiming().getStart());

            // time
            String sTime = SolutionUtils.formatMinutes(solutions[i].getTiming().getElapsedTime(), this.configurationManager.getConfiguration("TIMER-PRECISION"), false);

            tableModel.addRow(new Object[] {
                solutions.length - i,
                sStart,
                sTime,
                solutions[i].getPenalty(),
                solutions[i].getComment(),
                solutions[i].getScramble().getRawSequence(),
            });
        }

        SortButtonRenderer renderer = new SortButtonRenderer();

        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new HeaderListener(header, renderer));
    }

    class HeaderListener extends MouseAdapter {
        JTableHeader header;
        SortButtonRenderer renderer;

        HeaderListener(JTableHeader header,SortButtonRenderer renderer) {
            this.header   = header;
            this.renderer = renderer;
        }

        public void mousePressed(MouseEvent e) {
            int col = header.columnAtPoint(e.getPoint());
            int sortCol = header.getTable().convertColumnIndexToModel(col);
            renderer.setPressedColumn(col);
            renderer.setSelectedColumn(col);
            header.repaint();

            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }

            boolean isAscent;
            isAscent = SortButtonRenderer.DOWN == renderer.getState(col);
            ((CustomTableModel)header.getTable().getModel())
                    .sortByColumn(sortCol, isAscent);
        }

        public void mouseReleased(MouseEvent e) {
            int col = header.columnAtPoint(e.getPoint());
            renderer.setPressedColumn(-1);                // clear
            header.repaint();
        }
    }
}



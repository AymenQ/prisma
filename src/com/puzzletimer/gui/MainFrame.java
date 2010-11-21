package com.puzzletimer.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.graphics.Panel3D;
import com.puzzletimer.models.Category;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ColorManager;
import com.puzzletimer.state.ConfigurationManager;
import com.puzzletimer.state.ScrambleListener;
import com.puzzletimer.state.ScrambleManager;
import com.puzzletimer.state.SessionListener;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.state.TimerListener;
import com.puzzletimer.state.TimerManager;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.timer.KeyboardTimer;
import com.puzzletimer.timer.StackmatTimer;
import com.puzzletimer.timer.Timer;
import com.puzzletimer.tips.TipperProvider;
import com.puzzletimer.util.SolutionUtils;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private class ScramblePanel extends JPanel {
        private ScrambleViewerPanel scrambleViewerPanel;

        public ScramblePanel(ScrambleManager scrambleManager) {
            createComponents();

            scrambleManager.addScrambleListener(new ScrambleListener() {
                @Override
                public void scrambleChanged(Scramble scramble) {
                    setScramble(scramble);
                }
            });
        }

        public void setScrambleViewerPanel(ScrambleViewerPanel scrambleViewerPanel) {
            this.scrambleViewerPanel = scrambleViewerPanel;
        }

        private void createComponents() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 3));
        }

        private void setScramble(final Scramble scramble) {
            removeAll();

            for (int i = 0; i < scramble.getSequence().length; i++) {
                JLabel labelMove = new JLabel(scramble.getSequence()[i]);
                labelMove.setFont(new Font("Arial", Font.PLAIN, 18));
                labelMove.setCursor(new Cursor(Cursor.HAND_CURSOR));

                final int length = i + 1;
                labelMove.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ScramblePanel.this.scrambleViewerPanel.setScramble(
                            new Scramble(
                                scramble.getScramblerId(),
                                Arrays.copyOf(scramble.getSequence(), length)));
                    }
                });

                add(labelMove, "gap 10");
            }

            revalidate();
            repaint();
        }
    }

    private class TimerPanel extends JPanel {
        private ImageIcon iconLeft;
        private ImageIcon iconLeftPressed;
        private ImageIcon iconRight;
        private ImageIcon iconRightPressed;

        private JLabel labelLeftHand;
        private JLabel labelTime;
        private JLabel labelRightHand;

        public TimerPanel(TimerManager timerManager) {
            createComponents();

            timerManager.addTimerListener(new TimerListener() {
                @Override
                public void leftHandPressed() {
                    TimerPanel.this.labelLeftHand.setIcon(TimerPanel.this.iconLeftPressed);
                }

                @Override
                public void leftHandReleased() {
                    TimerPanel.this.labelLeftHand.setIcon(TimerPanel.this.iconLeft);
                }

                @Override
                public void rightHandPressed() {
                    TimerPanel.this.labelRightHand.setIcon(TimerPanel.this.iconRightPressed);
                }

                @Override
                public void rightHandReleased() {
                    TimerPanel.this.labelRightHand.setIcon(TimerPanel.this.iconRight);
                }

                @Override
                public void timerRunning(Timing timing) {
                    TimerPanel.this.labelTime.setText(SolutionUtils.formatMinutes(timing.getElapsedTime()));
                }

                @Override
                public void timerStopped(Timing timing) {
                    TimerPanel.this.labelTime.setText(SolutionUtils.formatMinutes(timing.getElapsedTime()));
                }
            });
        }

        private void createComponents() {
            // icons
            this.iconLeft =
                new ImageIcon(getClass().getResource("/com/puzzletimer/resources/left.png"));
            this.iconLeftPressed =
                new ImageIcon(getClass().getResource("/com/puzzletimer/resources/leftPressed.png"));
            this.iconRight =
                new ImageIcon(getClass().getResource("/com/puzzletimer/resources/right.png"));
            this.iconRightPressed =
                new ImageIcon(getClass().getResource("/com/puzzletimer/resources/rightPressed.png"));

            setLayout(new MigLayout("fillx", "[fill][pref!][fill]"));

            // labelLeftHand
            this.labelLeftHand = new JLabel(this.iconLeft);
            add(this.labelLeftHand);

            // labelTime
            this.labelTime = new JLabel("00:00.00");
            this.labelTime.setFont(new Font("Arial", Font.BOLD, 108));
            add(this.labelTime);

            // labelRightHand
            this.labelRightHand = new JLabel(this.iconRight);
            add(this.labelRightHand);
        }
    }

    private class TimesScrollPane extends JScrollPane {
        private SolutionManager solutionManager;

        private JPanel panel;

        public TimesScrollPane(SolutionManager solutionManager, SessionManager sessionManager) {
            this.solutionManager = solutionManager;

            createComponents();

            sessionManager.addSessionListener(new SessionListener() {
                @Override
                public void solutionsUpdated(Solution[] solutions) {
                    setSolutions(solutions);
                }
            });
        }

        private void createComponents() {
            // scroll doesn't work without this
            setPreferredSize(new Dimension(0, 0));

            // panel
            this.panel = new JPanel(
                new MigLayout(
                    "center",
                    "0[right]8[pref!]16[pref!]8[pref!]16[pref!]0",
                    ""));
        }

        private void setSolutions(final Solution[] solutions) {
            this.panel.removeAll();

            for (int i = 0; i < solutions.length; i++) {
                final Solution solution = solutions[i];

                JLabel labelIndex = new JLabel(Integer.toString(solutions.length - i) + ".");
                labelIndex.setFont(new Font("Tahoma", Font.BOLD, 13));
                this.panel.add(labelIndex);

                JLabel labelTime = new JLabel(SolutionUtils.formatMinutes(solutions[i].getTiming().getElapsedTime()));
                labelTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
                this.panel.add(labelTime);

                final JLabel labelPlus2 = new JLabel("+2");
                labelPlus2.setFont(new Font("Tahoma", Font.PLAIN, 13));
                if (!solution.getPenalty().equals("+2")) {
                    labelPlus2.setForeground(Color.LIGHT_GRAY);
                }
                labelPlus2.setCursor(new Cursor(Cursor.HAND_CURSOR));
                labelPlus2.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!solution.getPenalty().equals("+2")) {
                            TimesScrollPane.this.solutionManager.updateSolution(
                                solution.setPenalty("+2"));
                        } else if (solution.getPenalty().equals("+2")) {
                            TimesScrollPane.this.solutionManager.updateSolution(
                                solution.setPenalty(""));
                        }
                    }
                });
                this.panel.add(labelPlus2);

                final JLabel labelDNF = new JLabel("DNF");
                labelDNF.setFont(new Font("Tahoma", Font.PLAIN, 13));
                if (!solution.getPenalty().equals("DNF")) {
                    labelDNF.setForeground(Color.LIGHT_GRAY);
                }
                labelDNF.setCursor(new Cursor(Cursor.HAND_CURSOR));
                labelDNF.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!solution.getPenalty().equals("DNF")) {
                            TimesScrollPane.this.solutionManager.updateSolution(
                                solution.setPenalty("DNF"));
                        } else if (solution.getPenalty().equals("DNF")) {
                            TimesScrollPane.this.solutionManager.updateSolution(
                                solution.setPenalty(""));
                        }
                    }
                });
                this.panel.add(labelDNF);

                JLabel labelX = new JLabel();
                labelX.setIcon(new ImageIcon(getClass().getResource("/com/puzzletimer/resources/x.png")));
                labelX.setCursor(new Cursor(Cursor.HAND_CURSOR));
                labelX.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        TimesScrollPane.this.solutionManager.removeSolution(solution);
                    }
                });
                this.panel.add(labelX, "wrap");
            }

            setViewportView(this.panel);
        }
    }

    private class StatisticsPanel extends JPanel {
        private JLabel labelMean;
        private JLabel labelStandardDeviation;
        private JLabel labelBest;
        private JLabel labelMeanOf3;
        private JLabel labelAverageOf5;
        private JLabel labelStandardDeviationOf3;
        private JLabel labelBestOf3;
        private JLabel labelMeanOf10;
        private JLabel labelAverageOf12;
        private JLabel labelStandardDeviationOf10;
        private JLabel labelBestOf10;

        private StatisticsPanel(SessionManager sessionManager) {
            createComponents();

            final JLabel[] labels = {
                this.labelMean,
                this.labelStandardDeviation,
                this.labelBest,
                this.labelMeanOf3,
                this.labelAverageOf5,
                this.labelStandardDeviationOf3,
                this.labelBestOf3,
                this.labelMeanOf10,
                this.labelAverageOf12,
                this.labelStandardDeviationOf10,
                this.labelBestOf10,
            };

            final StatisticalMeasure[] measures = {
                new Mean(1, Integer.MAX_VALUE),
                new StandardDeviation(1, Integer.MAX_VALUE),
                new Best(1, Integer.MAX_VALUE),
                new Mean(3, 3),
                new Average(5, 5),
                new StandardDeviation(3, 3),
                new Best(3, 3),
                new Mean(10, 10),
                new Average(12, 12),
                new StandardDeviation(10, 10),
                new Best(10, 10),
            };

            sessionManager.addSessionListener(new SessionListener() {
                @Override
                public void solutionsUpdated(Solution[] solutions) {
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
                    }
                }
            });
        }

        private void createComponents() {
            setLayout(
                new MigLayout(
                    "center",
                    "[pref!,right]8[pref!]",
                    "1[pref!]1[pref!]1[pref!]6[pref!]1[pref!]1[pref!]1[pref!]6[pref!]1[pref!]1[pref!]1[pref!]1"));

            // labelMean
            JLabel labelMeanDescription = new JLabel("Mean:");
            labelMeanDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelMeanDescription);

            this.labelMean = new JLabel("XX:XX.XX");
            add(this.labelMean, "wrap");

            // labelStandardDeviation
            JLabel labelStandardDeviationDescription = new JLabel("Standard Deviation:");
            labelStandardDeviationDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelStandardDeviationDescription);

            this.labelStandardDeviation = new JLabel("XX:XX.XX");
            add(this.labelStandardDeviation, "wrap");

            // labelBest
            JLabel labelBestDescription = new JLabel("Best Time:");
            labelBestDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelBestDescription);

            this.labelBest = new JLabel("XX:XX.XX");
            add(this.labelBest, "wrap");

            // labelMeanOf3
            JLabel labelMeanOf3Description = new JLabel("Mean (last 3):");
            labelMeanOf3Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelMeanOf3Description);

            this.labelMeanOf3 = new JLabel("XX:XX.XX");
            add(this.labelMeanOf3, "wrap");

            // labelAverageOf5
            JLabel labelAverageOf5Description = new JLabel("Average (last 5):");
            labelAverageOf5Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelAverageOf5Description);

            this.labelAverageOf5 = new JLabel("XX:XX.XX");
            add(this.labelAverageOf5, "wrap");

            // labelStandardDeviationOf3
            JLabel labelStandardDeviationOf3Description = new JLabel("Standard Deviation (last 3):");
            labelStandardDeviationOf3Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelStandardDeviationOf3Description);

            this.labelStandardDeviationOf3 = new JLabel("XX:XX.XX");
            add(this.labelStandardDeviationOf3, "wrap");

            // labelBestOf3
            JLabel labelBestOf3Description = new JLabel("Best Time (last 3):");
            labelBestOf3Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelBestOf3Description);

            this.labelBestOf3 = new JLabel("XX:XX.XX");
            add(this.labelBestOf3, "wrap");

            // labelMeanOf10
            JLabel labelMeanOf10Description = new JLabel("Mean (last 10):");
            labelMeanOf10Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelMeanOf10Description);

            this.labelMeanOf10 = new JLabel("XX:XX.XX");
            add(this.labelMeanOf10, "wrap");

            // labelAverageOf12
            JLabel labelAverageOf12Description = new JLabel("Average (last 12):");
            labelAverageOf12Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelAverageOf12Description);

            this.labelAverageOf12 = new JLabel("XX:XX.XX");
            add(this.labelAverageOf12, "wrap");

            // labelStandardDeviationOf10
            JLabel labelStandardDeviationOf10Description = new JLabel("Standard Deviation (last 10):");
            labelStandardDeviationOf10Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelStandardDeviationOf10Description);

            this.labelStandardDeviationOf10 = new JLabel("XX:XX.XX");
            add(this.labelStandardDeviationOf10, "wrap");

            // labelBestOf10
            JLabel labelBestOf10Description = new JLabel("Best Time (last 10):");
            labelBestOf10Description.setFont(new Font("Tahoma", Font.BOLD, 11));
            add(labelBestOf10Description);

            this.labelBestOf10 = new JLabel("XX:XX.XX");
            add(this.labelBestOf10, "wrap");
        }
    }

    private class ScrambleViewerPanel extends JPanel {
        private PuzzleProvider puzzleProvider;
        private ColorManager colorManager;
        private ScramblerProvider scramblerProvider;

        private Panel3D panel3D;

        public ScrambleViewerPanel(
                PuzzleProvider puzzleProvider,
                ColorManager colorManager,
                ScramblerProvider scramblerProvider,
                ScrambleManager scrambleManager) {
            this.puzzleProvider = puzzleProvider;
            this.colorManager = colorManager;
            this.scramblerProvider = scramblerProvider;

            createComponents();

            scrambleManager.addScrambleListener(new ScrambleListener() {
                @Override
                public void scrambleChanged(Scramble scramble) {
                    setScramble(scramble);
                }
            });
        }

        private void createComponents() {
            setLayout(new MigLayout("fill", "0[fill]0", "0[fill]0"));

            // panel 3d
            this.panel3D = new Panel3D();
            this.panel3D.setFocusable(false);
            add(this.panel3D);
        }

        public void setScramble(Scramble scramble) {
            Scrambler scrambler = this.scramblerProvider.get(scramble.getScramblerId());
            Puzzle puzzle = this.puzzleProvider.get(scrambler.getScramblerInfo().getPuzzleId());
            ColorScheme colorScheme = this.colorManager.getColorScheme(puzzle.getPuzzleInfo().getPuzzleId());
            this.panel3D.mesh = puzzle.getScrambledPuzzleMesh(colorScheme, scramble.getSequence());
            this.panel3D.repaint();
        }
    }

    private ConfigurationManager configurationManager;
    private TimerManager timerManager;
    private PuzzleProvider puzzleProvider;
    private ColorManager colorManager;
    private ScrambleParserProvider scrambleParserProvider;
    private ScramblerProvider scramblerProvider;
    private CategoryManager categoryManager;
    private ScrambleManager scrambleManager;
    private SolutionManager solutionManager;
    private SessionManager sessionManager;

    private JMenu menuFile;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemTips;
    private JMenuItem menuItemScrambleQueue;
    private JMenuItem menuItemHistory;
    private JMenuItem menuItemSessionSummary;
    private JMenu menuCategory;
    private JMenuItem menuColorScheme;
    private JRadioButtonMenuItem menuItemCtrlKeys;
    private JRadioButtonMenuItem menuItemSpaceKey;
    private JRadioButtonMenuItem menuItemStackmatTimer;
    private JMenuItem menuItemAbout;
    private ScramblePanel scramblePanel;
    private TimerPanel timerPanel;
    private TimesScrollPane timesScrollPane;
    private StatisticsPanel statisticsPanel;
    private ScrambleViewerPanel scrambleViewerPanel;

    private TipsFrame tipsFrame;
    private ScrambleQueueFrame scrambleQueueFrame;
    private HistoryFrame historyFrame;
    private SessionSummaryFrame sessionSummaryFrame;
    private CategoryManagerFrame categoryManagerDialog;
    private ColorSchemeFrame colorSchemeFrame;

    private AudioFormat audioFormat;
    private Mixer.Info mixerInfo;
    private JMenu stackmatTimerInputDevice;
    private ButtonGroup stackmatTimerInputDeviceGroup;

    public MainFrame(
            ConfigurationManager configurationManager,
            TimerManager timerManager,
            PuzzleProvider puzzleProvider,
            ColorManager colorManager,
            ScrambleParserProvider scrambleParserProvider,
            ScramblerProvider scramblerProvider,
            CategoryManager categoryManager,
            ScrambleManager scrambleManager,
            SolutionManager solutionManager,
            SessionManager sessionManager) {
        this.puzzleProvider = puzzleProvider;
        this.scrambleParserProvider = scrambleParserProvider;
        this.scramblerProvider = scramblerProvider;
        this.configurationManager = configurationManager;
        this.timerManager = timerManager;
        this.categoryManager = categoryManager;
        this.scrambleManager = scrambleManager;
        this.solutionManager = solutionManager;
        this.sessionManager = sessionManager;
        this.colorManager = colorManager;

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(getMinimumSize());

        createComponents();

        // timer configuration
        this.audioFormat = new AudioFormat(8000, 8, 1, true, false);
        this.mixerInfo = null;

        String stackmatTimerInputDeviceName =
            this.configurationManager.getConfiguration("STACKMAT-TIMER-INPUT-DEVICE");
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if (stackmatTimerInputDeviceName.equals(mixerInfo.getName())) {
                this.mixerInfo = mixerInfo;
                break;
            }
        }

        String timerTriggerId =
            this.configurationManager.getConfiguration("TIMER-TRIGGER");
        if (timerTriggerId.equals("KEYBOARD-TIMER-CONTROL")) {
            this.menuItemCtrlKeys.setSelected(true);
            this.timerManager.setTimer(
                new KeyboardTimer(this.timerManager, this, KeyEvent.VK_CONTROL));
        } else if (timerTriggerId.equals("KEYBOARD-TIMER-SPACE")) {
            this.menuItemSpaceKey.setSelected(true);
            this.timerManager.setTimer(
                new KeyboardTimer(this.timerManager, this, KeyEvent.VK_SPACE));
        } else if (timerTriggerId.equals("STACKMAT-TIMER")) {
            if (this.mixerInfo != null) {
                TargetDataLine targetDataLine = null;
                try {
                    targetDataLine = AudioSystem.getTargetDataLine(MainFrame.this.audioFormat, MainFrame.this.mixerInfo);
                    targetDataLine.open(MainFrame.this.audioFormat);
                    this.menuItemStackmatTimer.setSelected(true);
                    this.timerManager.setTimer(
                        new StackmatTimer(this.timerManager, targetDataLine));
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                    // TODO: show error message?
                }
            } else {
                this.menuItemSpaceKey.setSelected(true);
                this.timerManager.setTimer(
                    new KeyboardTimer(this.timerManager, this, KeyEvent.VK_SPACE));
            }
        }

        // title
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                setTitle("Puzzle Timer - " + currentCategory.getDescription());
            }
        });

        // menuItemExit
        this.menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // menuItemTips
        this.menuItemTips.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.tipsFrame.setVisible(true);
            }
        });

        // menuItemScrambleQueue
        this.menuItemScrambleQueue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.scrambleQueueFrame.setVisible(true);
            }
        });

        // menuItemHistory
        this.menuItemHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.historyFrame.setVisible(true);
            }
        });

        // menuItemSessionSummary
        this.menuItemSessionSummary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.sessionSummaryFrame.setVisible(true);
            }
        });

        // menuCategory
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                MainFrame.this.menuCategory.removeAll();

                // category manager
                JMenuItem menuItemCategoryManager = new JMenuItem("Category manager...");
                menuItemCategoryManager.setMnemonic(KeyEvent.VK_M);
                menuItemCategoryManager.setAccelerator(KeyStroke.getKeyStroke("ctrl alt C"));
                menuItemCategoryManager.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        MainFrame.this.categoryManagerDialog.setVisible(true);
                    }
                });
                MainFrame.this.menuCategory.add(menuItemCategoryManager);

                MainFrame.this.menuCategory.addSeparator();

                ButtonGroup categoryGroup = new ButtonGroup();

                // built-in categories
                class BuiltInCategory {
                    public Category category;
                    public char mnemonic;
                    public char accelerator;

                    public BuiltInCategory(Category category, char mnemonic, char accelerator) {
                        this.category = category;
                        this.mnemonic = mnemonic;
                        this.accelerator = accelerator;
                    }
                }

                BuiltInCategory[] builtInCategories = {
                    new BuiltInCategory(categories[0], '2', '2'),
                    new BuiltInCategory(categories[1], 'R', '3'),
                    new BuiltInCategory(categories[2], 'O', '\0'),
                    new BuiltInCategory(categories[3], 'B', '\0'),
                    new BuiltInCategory(categories[4], 'F', '\0'),
                    new BuiltInCategory(categories[5], '4', '4'),
                    new BuiltInCategory(categories[6], 'B', '\0'),
                    new BuiltInCategory(categories[7], '5', '5'),
                    new BuiltInCategory(categories[8], 'B', '\0'),
                    new BuiltInCategory(categories[9], '6', '6'),
                    new BuiltInCategory(categories[10], '7', '7'),
                    new BuiltInCategory(categories[11], 'C', 'K'),
                    new BuiltInCategory(categories[12], 'M', 'M'),
                    new BuiltInCategory(categories[13], 'P', 'P'),
                    new BuiltInCategory(categories[14], 'S', '1'),
                    new BuiltInCategory(categories[15], 'M', 'G'),
                    new BuiltInCategory(categories[16], 'M', 'A'),
                };

                for (final BuiltInCategory builtInCategory : builtInCategories) {
                    JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(builtInCategory.category.getDescription());
                    menuItemCategory.setMnemonic(builtInCategory.mnemonic);
                    if (builtInCategory.accelerator != '\0') {
                        menuItemCategory.setAccelerator(KeyStroke.getKeyStroke(builtInCategory.accelerator, InputEvent.CTRL_MASK));
                    }
                    menuItemCategory.setSelected(builtInCategory.category == currentCategory);
                    menuItemCategory.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.categoryManager.setCurrentCategory(builtInCategory.category);
                        }
                    });
                    MainFrame.this.menuCategory.add(menuItemCategory);
                    categoryGroup.add(menuItemCategory);
                }

                MainFrame.this.menuCategory.addSeparator();

                // user defined categories
                for (final Category category : categories) {
                    if (category.isUserDefined()) {
                        JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(category.getDescription());
                        menuItemCategory.setSelected(category == currentCategory);
                        menuItemCategory.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.categoryManager.setCurrentCategory(category);
                            }
                        });
                        MainFrame.this.menuCategory.add(menuItemCategory);
                        categoryGroup.add(menuItemCategory);
                    }
                }
            }
        });

        // menuColorScheme
        this.menuColorScheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.colorSchemeFrame.setVisible(true);
            }
        });

        // menuItemCtrlKeys
        this.menuItemCtrlKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = new KeyboardTimer(MainFrame.this.timerManager, MainFrame.this, KeyEvent.VK_CONTROL);
                MainFrame.this.timerManager.setTimer(timer);
            }
        });

        // menuItemSpaceKey
        this.menuItemSpaceKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = new KeyboardTimer(MainFrame.this.timerManager, MainFrame.this, KeyEvent.VK_SPACE);
                MainFrame.this.timerManager.setTimer(timer);
            }
        });

        // menuItemStackmatTimer
        this.menuItemStackmatTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainFrame.this.mixerInfo == null) {
                    MainFrame.this.timerManager.setTimer(null);
                    return;
                }

                TargetDataLine targetDataLine;
                try {
                    targetDataLine = AudioSystem.getTargetDataLine(MainFrame.this.audioFormat, MainFrame.this.mixerInfo);
                    targetDataLine.open(MainFrame.this.audioFormat);
                } catch (LineUnavailableException ex) {
                    // select the default timer
                    MainFrame.this.menuItemSpaceKey.setSelected(true);
                    MainFrame.this.timerManager.setTimer(
                        new KeyboardTimer(MainFrame.this.timerManager, MainFrame.this, KeyEvent.VK_SPACE));
                    return;
                }

                MainFrame.this.timerManager.setTimer(
                    new StackmatTimer(MainFrame.this.timerManager, targetDataLine));
            }
        });

        // menuItemDevice
        for (final Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Line.Info[] targetLinesInfo =
                AudioSystem.getTargetLineInfo(new Info(TargetDataLine.class, this.audioFormat));

            boolean validMixer = false;
            for (Line.Info lineInfo : targetLinesInfo) {
                if (AudioSystem.getMixer(mixerInfo).isLineSupported(lineInfo)) {
                    validMixer = true;
                    break;
                }
            }

            if (!validMixer) {
                continue;
            }

            JRadioButtonMenuItem menuItemDevice = new JRadioButtonMenuItem(mixerInfo.getName());
            menuItemDevice.setSelected(stackmatTimerInputDeviceName.equals(mixerInfo.getName()));
            menuItemDevice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    MainFrame.this.mixerInfo = mixerInfo;
                    MainFrame.this.configurationManager.setConfiguration(
                        "STACKMAT-TIMER-INPUT-DEVICE", mixerInfo.getName());
                }
            });
            this.stackmatTimerInputDevice.add(menuItemDevice);
            this.stackmatTimerInputDeviceGroup.add(menuItemDevice);

            if (MainFrame.this.mixerInfo == null) {
                menuItemDevice.setSelected(true);
                MainFrame.this.mixerInfo = mixerInfo;
                this.configurationManager.setConfiguration(
                    "STACKMAT-TIMER-INPUT-DEVICE", mixerInfo.getName());
            }
        }

        // menuItemAbout
        this.menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog aboutDialog = new AboutDialog(MainFrame.this, true);
                aboutDialog.setLocationRelativeTo(null);
                aboutDialog.setVisible(true);
            }
        });
    }

    private void createComponents() {
        // menuBar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // menuFile
        this.menuFile = new JMenu("File");
        this.menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(this.menuFile);

        // menuItemExit
        this.menuItemExit = new JMenuItem("Exit");
        this.menuItemExit.setMnemonic(KeyEvent.VK_X);
        this.menuFile.add(this.menuItemExit);

        // menuView
        JMenu menuView = new JMenu("View");
        menuView.setMnemonic(KeyEvent.VK_V);
        menuBar.add(menuView);

        // menuItemTips
        this.menuItemTips = new JMenuItem("Tips...");
        this.menuItemTips.setMnemonic(KeyEvent.VK_T);
        this.menuItemTips.setAccelerator(KeyStroke.getKeyStroke("ctrl alt T"));
        menuView.add(this.menuItemTips);

        // menuItemScrambleQueue
        this.menuItemScrambleQueue = new JMenuItem("Scramble queue...");
        this.menuItemScrambleQueue.setMnemonic(KeyEvent.VK_Q);
        this.menuItemScrambleQueue.setAccelerator(KeyStroke.getKeyStroke("ctrl alt Q"));
        menuView.add(this.menuItemScrambleQueue);

        // menuItemHistory
        this.menuItemHistory = new JMenuItem("History...");
        this.menuItemHistory.setMnemonic(KeyEvent.VK_H);
        this.menuItemHistory.setAccelerator(KeyStroke.getKeyStroke("ctrl alt H"));
        menuView.add(this.menuItemHistory);

        // menuItemSessionSummary
        this.menuItemSessionSummary = new JMenuItem("Session summary...");
        this.menuItemSessionSummary.setMnemonic(KeyEvent.VK_S);
        this.menuItemSessionSummary.setAccelerator(KeyStroke.getKeyStroke("ctrl alt S"));
        menuView.add(this.menuItemSessionSummary);

        // menuCategory
        this.menuCategory = new JMenu("Category");
        this.menuCategory.setMnemonic(KeyEvent.VK_C);
        menuBar.add(this.menuCategory);

        // menuOptions
        JMenu menuOptions = new JMenu("Options");
        menuOptions.setMnemonic(KeyEvent.VK_O);
        menuBar.add(menuOptions);

        // menuColorScheme
        this.menuColorScheme = new JMenuItem("Color scheme...");
        this.menuColorScheme.setMnemonic(KeyEvent.VK_C);
        this.menuColorScheme.setAccelerator(KeyStroke.getKeyStroke("ctrl alt K"));
        menuOptions.add(this.menuColorScheme);

        // menuTimerTrigger
        JMenu menuTimerTrigger = new JMenu("Timer trigger");
        menuTimerTrigger.setMnemonic(KeyEvent.VK_T);
        menuOptions.add(menuTimerTrigger);
        ButtonGroup timerTriggerGroup = new ButtonGroup();

        // menuItemCtrlKeys
        this.menuItemCtrlKeys = new JRadioButtonMenuItem("Ctrl keys");
        this.menuItemCtrlKeys.setMnemonic(KeyEvent.VK_C);
        this.menuItemCtrlKeys.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
        menuTimerTrigger.add(this.menuItemCtrlKeys);
        timerTriggerGroup.add(this.menuItemCtrlKeys);

        // menuItemSpaceKey
        this.menuItemSpaceKey = new JRadioButtonMenuItem("Space key");
        this.menuItemSpaceKey.setMnemonic(KeyEvent.VK_S);
        this.menuItemSpaceKey.setAccelerator(KeyStroke.getKeyStroke(' ', InputEvent.CTRL_MASK));
        menuTimerTrigger.add(this.menuItemSpaceKey);
        timerTriggerGroup.add(this.menuItemSpaceKey);

        // menuItemStackmatTimer
        this.menuItemStackmatTimer = new JRadioButtonMenuItem("Stackmat timer");
        this.menuItemStackmatTimer.setMnemonic(KeyEvent.VK_T);
        this.menuItemStackmatTimer.setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
        menuTimerTrigger.add(this.menuItemStackmatTimer);
        timerTriggerGroup.add(this.menuItemStackmatTimer);

        // menuStackmatTimerInputDevice
        this.stackmatTimerInputDevice = new JMenu("Stackmat timer input device");
        menuTimerTrigger.setMnemonic(KeyEvent.VK_S);
        menuOptions.add(this.stackmatTimerInputDevice);
        this.stackmatTimerInputDeviceGroup = new ButtonGroup();

        //menuHelp
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // menuItemAbout
        this.menuItemAbout = new JMenuItem("About...");
        this.menuItemAbout.setMnemonic(KeyEvent.VK_A);
        menuHelp.add(this.menuItemAbout);

        // panelMain
        JPanel panelMain = new JPanel(new TableLayout(new double[][] {
            { 3, 0.3, 0.4, 0.3, 3 },
            { 20, 0.5, TableLayout.PREFERRED, 0.5, TableLayout.PREFERRED, 3 },
        }));
        add(panelMain);

        // panelScramble
        this.scramblePanel = new ScramblePanel(this.scrambleManager);
        panelMain.add(this.scramblePanel, "1, 1, 3, 1");

        // timer panel
        this.timerPanel = new TimerPanel(this.timerManager);
        panelMain.add(this.timerPanel, "1, 2, 3, 2");

        // times scroll pane
        this.timesScrollPane = new TimesScrollPane(this.solutionManager, this.sessionManager);
        this.timesScrollPane.setBorder(BorderFactory.createTitledBorder("Times"));
        this.timesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelMain.add(this.timesScrollPane, "1, 4");

        // statistics panel
        this.statisticsPanel = new StatisticsPanel(this.sessionManager);
        this.statisticsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        panelMain.add(this.statisticsPanel, "2, 4");

        // scramble viewer panel
        this.scrambleViewerPanel = new ScrambleViewerPanel(
            this.puzzleProvider,
            this.colorManager,
            this.scramblerProvider,
            this.scrambleManager);
        this.scrambleViewerPanel.setBorder(BorderFactory.createTitledBorder("Scramble"));
        panelMain.add(this.scrambleViewerPanel, "3, 4");

        this.scramblePanel.setScrambleViewerPanel(this.scrambleViewerPanel);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/com/puzzletimer/resources/icon.png"));

        // tips frame
        TipperProvider tipperProvider = new TipperProvider();
        this.tipsFrame = new TipsFrame(
            this.puzzleProvider,
            tipperProvider,
            this.scramblerProvider,
            this.categoryManager,
            this.scrambleManager);
        this.tipsFrame.setLocationRelativeTo(null);
        this.tipsFrame.setIconImage(icon);

        // scramble queue frame
        this.scrambleQueueFrame = new ScrambleQueueFrame(
            this.scrambleParserProvider,
            this.scramblerProvider,
            this.categoryManager,
            this.scrambleManager);
        this.scrambleQueueFrame.setLocationRelativeTo(null);
        this.scrambleQueueFrame.setIconImage(icon);

        // history frame
        this.historyFrame = new HistoryFrame(
            this.categoryManager,
            this.solutionManager);
        this.historyFrame.setLocationRelativeTo(null);
        this.historyFrame.setIconImage(icon);

        // session summary frame
        this.sessionSummaryFrame = new SessionSummaryFrame(
            this.categoryManager,
            this.sessionManager);
        this.sessionSummaryFrame.setLocationRelativeTo(null);
        this.sessionSummaryFrame.setIconImage(icon);

        // category manager dialog
        this.categoryManagerDialog = new CategoryManagerFrame(
            this.puzzleProvider,
            this.scramblerProvider,
            this.categoryManager);
        this.categoryManagerDialog.setLocationRelativeTo(null);
        this.categoryManagerDialog.setIconImage(icon);

        // color scheme frame
        this.colorSchemeFrame = new ColorSchemeFrame(
            this.puzzleProvider,
            this.colorManager);
        this.colorSchemeFrame.setLocationRelativeTo(null);
        this.colorSchemeFrame.setIconImage(icon);
    }
}

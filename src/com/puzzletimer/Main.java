package com.puzzletimer;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.puzzletimer.graphics.Panel3D;
import com.puzzletimer.models.Category;
import com.puzzletimer.models.FullSolution;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleBuilder;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerBuilder;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ScrambleListener;
import com.puzzletimer.state.ScrambleManager;
import com.puzzletimer.state.SessionListener;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.state.SolutionListener;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.state.TimerManager;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.timer.KeyboardTimer;
import com.puzzletimer.timer.StackmatTimer;
import com.puzzletimer.timer.TimerListener;

@SuppressWarnings("serial")
public class Main extends JFrame {
    private HistoryFrame historyFrame;

    private TimerManager timerManager;
    private CategoryManager categoryManager;
    private ScrambleManager scrambleManager;
    private SolutionManager solutionManager;
    private SessionManager sessionManager;

    private AudioFormat audioFormat;
    private Mixer.Info mixerInfo;

    private boolean timerStopped;

    public Main() {
        Category defaultCategory = new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's Cube", false);

        // timer manager
        this.timerManager = new TimerManager(new KeyboardTimer(this, KeyEvent.VK_CONTROL));

        // categoryManager
        this.categoryManager = new CategoryManager(defaultCategory);

        // scramble manager
        this.scrambleManager = new ScrambleManager(ScramblerBuilder.getScrambler(defaultCategory.getScramblerId()));
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoryChanged(Category category) {
                Scrambler scrambler = ScramblerBuilder.getScrambler(category.getScramblerId());
                Main.this.scrambleManager.setScrambler(scrambler);
            }
        });

        // solution manager
        this.solutionManager = new SolutionManager();
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoryChanged(Category category) {
                Main.this.solutionManager.loadSolutions(new FullSolution[0]);
            }
        });
        this.timerManager.addTimerListener(new TimerListener() {
            @Override
            public void timerReady() {
                Main.this.timerStopped = false;
            }

            @Override
            public void timerStopped(Timing timing) {
                if (!Main.this.timerStopped) {
                    Scramble scramble =
                        new Scramble(
                            UUID.randomUUID(),
                            Main.this.categoryManager.getCurrentCategory().getCategoryId(),
                            Main.this.scrambleManager.getCurrentSequence());
                    Solution solution =
                        new Solution(
                            UUID.randomUUID(),
                            scramble.getScrambleId(),
                            timing,
                            "");
                    Main.this.solutionManager.addSolution(new FullSolution(solution, scramble));
                    Main.this.scrambleManager.changeScramble();
                }

                Main.this.timerStopped = true;
            }
        });

        // session manager
        this.sessionManager = new SessionManager();
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoryChanged(Category category) {
                Main.this.sessionManager.clearSession();
            }
        });
        this.solutionManager.addSolutionListener(new SolutionListener() {
            @Override
            public void solutionAdded(FullSolution solution) {
                Main.this.sessionManager.addSolution(solution);
            }

            @Override
            public void solutionRemoved(FullSolution solution) {
                Main.this.sessionManager.removeSolution(solution);
            }

            @Override
            public void solutionsUpdated(FullSolution[] solutions) {
                Main.this.sessionManager.notifyListeners();
            }
        });

        // stackmat timer input
        this.audioFormat = new AudioFormat(8000, 8, 1, true, false);
        this.mixerInfo = null;

        createComponents();

        // update screen
        this.categoryManager.notifyListeners();
    }

    private void createComponents() {
        // frameMain
        setMinimumSize(new Dimension(800, 600));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/com/puzzletimer/resources/icon.png")));
        setTitle("Puzzle Timer");

        // menu
        setJMenuBar(createMenuBar());

        // panelMain
        JPanel panelMain = new JPanel(new TableLayout(new double[][] {
            { 3, 0.3, 0.4, 0.3, 3 },
            { 20, 0.5, TableLayout.PREFERRED, 0.5, TableLayout.PREFERRED, 3 },
        }));
        add(panelMain);

        // panelScramble
        final JPanel panelScramble = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
        panelScramble.setPreferredSize(new Dimension(10000, 150));
        this.scrambleManager.addScrambleListener(new ScrambleListener() {
            @Override
            public void scrambleChanged(String[] sequence) {
                panelScramble.removeAll();

                for (String move : sequence) {
                    JLabel label = new JLabel(move);
                    label.setFont(new Font("Arial", Font.PLAIN, 18));
                    panelScramble.add(label);
                }

                panelScramble.revalidate();
                panelScramble.repaint();
            }
        });
        panelMain.add(panelScramble, "1, 1, 3, 1");

        // timer panel
        panelMain.add(createTimerPanel(), "1, 2, 3, 2");

        // times panel
        panelMain.add(createTimesPanel(), "1, 4");

        // statistics panel
        panelMain.add(createStatisticsPanel(), "2, 4");

        // scramble panel
        panelMain.add(createScramblePanel(), "3, 4");

        // history frame
        this.historyFrame = new HistoryFrame(this.categoryManager, this.solutionManager);
    }

    private JMenuBar createMenuBar() {
        // menuBar
        JMenuBar menuBar = new JMenuBar();

        // menuFile
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        // menuItemExit
        final JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_X);
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuFile.add(menuItemExit);

        // menuView
        JMenu menuView = new JMenu("View");
        menuView.setMnemonic(KeyEvent.VK_V);
        menuBar.add(menuView);

        // menuItemHistory
        JMenuItem menuItemHistory = new JMenuItem("History...");
        menuItemHistory.setMnemonic(KeyEvent.VK_H);
        menuItemHistory.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menuItemHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.this.historyFrame.setVisible(true);
            }
        });
        menuView.add(menuItemHistory);

        // menuCategory
        final JMenu menuCategory = new JMenu("Category");
        menuCategory.setMnemonic(KeyEvent.VK_C);
        menuBar.add(menuCategory);
        ButtonGroup categoryGroup = new ButtonGroup();

        // built-in categories
        class BuiltInCategory {
            public Category category;
            public char mnemonic;
            public char accelerator;
            public boolean isDefault;

            public BuiltInCategory(Category category, char mnemonic, char accelerator, boolean isDefault) {
                this.category = category;
                this.mnemonic = mnemonic;
                this.accelerator = accelerator;
                this.isDefault = isDefault;
            }
        }

        BuiltInCategory[] builtInCategories = {
            new BuiltInCategory(new Category(null, "2x2x2-CUBE-RANDOM", "2x2x2 cube", false), '2', '2', false),
            new BuiltInCategory(new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube", false), 'R', '3', true),
            new BuiltInCategory(new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube one-handed", false), 'O', '\0', false),
            new BuiltInCategory(new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube blindfolded", false), 'B', '\0', false),
            new BuiltInCategory(new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube with feet", false), 'F', '\0', false),
            new BuiltInCategory(new Category(null, "4x4x4-CUBE-RANDOM", "4x4x4 cube", false), '4', '4', false),
            new BuiltInCategory(new Category(null, "4x4x4-CUBE-RANDOM", "4x4x4 blindfolded", false), 'B', '\0', false),
            new BuiltInCategory(new Category(null, "5x5x5-CUBE-RANDOM", "5x5x5 cube", false), '5', '5', false),
            new BuiltInCategory(new Category(null, "5x5x5-CUBE-RANDOM", "5x5x5 blindfolded", false), 'B', '\0', false),
            new BuiltInCategory(new Category(null, "MEGAMINX-RANDOM", "Megaminx", false), 'M', 'M', false),
            new BuiltInCategory(new Category(null, "PYRAMINX-RANDOM", "Pyraminx", false), 'P', 'P', false),
            new BuiltInCategory(new Category(null, "SQUARE-1-RANDOM", "Square-1", false), 'S', '1', false),
            new BuiltInCategory(new Category(null, "EMPTY", "Rubik's clock", false), 'C', 'K', false),
            new BuiltInCategory(new Category(null, "EMPTY", "Rubik's magic", false), 'M', 'G', false),
            new BuiltInCategory(new Category(null, "EMPTY", "Master magic", false), 'M', 'A', false),
        };

        for (final BuiltInCategory builtInCategory : builtInCategories) {
            JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(builtInCategory.category.getDescription());
            menuItemCategory.setMnemonic(builtInCategory.mnemonic);
            if (builtInCategory.accelerator != '\0') {
                menuItemCategory.setAccelerator(KeyStroke.getKeyStroke(builtInCategory.accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            }
            menuItemCategory.setSelected(builtInCategory.isDefault);
            menuItemCategory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.this.categoryManager.setCategory(builtInCategory.category);
                }
            });
            menuCategory.add(menuItemCategory);
            categoryGroup.add(menuItemCategory);
        }

        menuCategory.addSeparator();

        // user defined categories
        Category[] categories = {
          new Category(null, "RUBIKS-CUBE-RANDOM-EDGES", "Rubik's cube - random edges", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-EDGES-PERMUTATION", "Rubik's cube - random edges permutation", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-EDGES-ORIENTATION", "Rubik's cube - random edges orientation", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-CORNERS", "Rubik's cube - random corners", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-CORNERS-PERMUTATION", "Rubik's cube - random corners permutation", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-CORNERS-ORIENTATION", "Rubik's cube - random corners orientation", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-LAST-LAYER", "Rubik's cube - random last layer", true),
          new Category(null, "RUBIKS-CUBE-RANDOM-LAST-LAYER-PERMUTATION", "Rubik's cube - random last layer permutation", true),
          new Category(null, "RUBIKS-CUBE-EASY-CROSS", "Rubik's cube - easy cross", true),
          new Category(null, "RUBIKS-CUBE-HARD-CROSS", "Rubik's cube - hard cross", true),
        };

        for (final Category category : categories) {
            JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(category.getDescription());
            menuItemCategory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.this.categoryManager.setCategory(category);
                }
            });
            menuCategory.add(menuItemCategory);
            categoryGroup.add(menuItemCategory);
        }

        // menuOptions
        final JMenu menuOptions = new JMenu("Options");
        menuOptions.setMnemonic(KeyEvent.VK_O);
        menuBar.add(menuOptions);

        // menuTimerTrigger
        final JMenu menuTimerTrigger = new JMenu("Timer trigger");
        menuTimerTrigger.setMnemonic(KeyEvent.VK_T);
        menuOptions.add(menuTimerTrigger);
        ButtonGroup timerTriggerGroup = new ButtonGroup();

        // menuItemCtrlKeys
        final JRadioButtonMenuItem menuItemCtrlKeys = new JRadioButtonMenuItem("Ctrl keys");
        menuItemCtrlKeys.setMnemonic(KeyEvent.VK_C);
        menuItemCtrlKeys.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        menuItemCtrlKeys.setSelected(true);
        menuItemCtrlKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.this.timerStopped = false;
                Main.this.timerManager.setTimer(new KeyboardTimer(Main.this, KeyEvent.VK_CONTROL));
            }
        });
        menuTimerTrigger.add(menuItemCtrlKeys);
        timerTriggerGroup.add(menuItemCtrlKeys);

        // menuItemSpaceKey
        final JRadioButtonMenuItem menuItemSpaceKey = new JRadioButtonMenuItem("Space key");
        menuItemSpaceKey.setMnemonic(KeyEvent.VK_S);
        menuItemSpaceKey.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        menuItemSpaceKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.this.timerStopped = false;
                Main.this.timerManager.setTimer(new KeyboardTimer(Main.this, KeyEvent.VK_SPACE));
            }
        });
        menuTimerTrigger.add(menuItemSpaceKey);
        timerTriggerGroup.add(menuItemSpaceKey);

        // menuItemStackmatTimer
        final JRadioButtonMenuItem menuItemStackmatTimer = new JRadioButtonMenuItem("Stackmat timer");
        menuItemStackmatTimer.setMnemonic(KeyEvent.VK_T);
        menuItemStackmatTimer.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        menuItemStackmatTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.this.timerStopped = true;

                if (Main.this.mixerInfo == null) {
                    return;
                }

                TargetDataLine targetDataLine;
                try {
                    targetDataLine = AudioSystem.getTargetDataLine(Main.this.audioFormat, Main.this.mixerInfo);
                    targetDataLine.open(Main.this.audioFormat);
                } catch (LineUnavailableException ex) {
                    // select the default timer
                    menuItemCtrlKeys.setSelected(true);
                    Main.this.timerManager.setTimer(new KeyboardTimer(Main.this, KeyEvent.VK_CONTROL));
                    return;
                }

                Main.this.timerManager.setTimer(new StackmatTimer(targetDataLine));
            }
        });
        menuTimerTrigger.add(menuItemStackmatTimer);
        timerTriggerGroup.add(menuItemStackmatTimer);

        // menuStackmatTimerInputDevice
        JMenu stackmatTimerInputDevice = new JMenu("Stackmat timer input device");
        menuTimerTrigger.setMnemonic(KeyEvent.VK_S);
        menuOptions.add(stackmatTimerInputDevice);
        ButtonGroup stackmatTimerInputDeviceGroup = new ButtonGroup();

        // menuItemNone
        JRadioButtonMenuItem menuItemNone = new JRadioButtonMenuItem("None");
        menuItemNone.setSelected(true);
        menuItemNone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Main.this.mixerInfo = null;
            }
        });
        stackmatTimerInputDevice.add(menuItemNone);
        stackmatTimerInputDeviceGroup.add(menuItemNone);

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
            menuItemDevice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Main.this.mixerInfo = mixerInfo;
                }
            });
            stackmatTimerInputDevice.add(menuItemDevice);
            stackmatTimerInputDeviceGroup.add(menuItemDevice);

            if (Main.this.mixerInfo == null) {
                Main.this.mixerInfo = mixerInfo;
                menuItemDevice.setSelected(true);
            }
        }

        //menuHelp
        final JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // menuItemAbout
        final JMenuItem menuItemAbout = new JMenuItem("About...");
        final JFrame frame = this;
        menuItemAbout.setMnemonic(KeyEvent.VK_A);
        menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Puzzle Timer 0.2\r\n2010\r\nhttp://www.puzzletimer.com\r\nWalter Souza <walterprs@gmail.com>", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuHelp.add(menuItemAbout);

        return menuBar;
    }

    private JPanel createTimerPanel() {
        // panelTime
        JPanel panelTimer = new JPanel(new TableLayout(new double[][] {
            { 0.35, TableLayout.PREFERRED, 0.15, TableLayout.PREFERRED, 0.15, TableLayout.PREFERRED, 0.35 },
            { TableLayout.FILL },
        }));

        // labelLeftHand
        final ImageIcon iconLeft = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/left.png"));
        final ImageIcon iconLeftPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/leftPressed.png"));

        final JLabel labelLeftHand = new JLabel(iconLeft);
        this.timerManager.addTimerListener(new TimerListener() {
            @Override
            public void leftHandPressed() {
                labelLeftHand.setIcon(iconLeftPressed);
            }

            @Override
            public void leftHandReleased() {
                labelLeftHand.setIcon(iconLeft);
            }
        });
        panelTimer.add(labelLeftHand, "1, 0");

        // labelTime
        final JLabel labelTime = new JLabel("00:00.00");
        labelTime.setFont(new Font("Arial", Font.BOLD, 108));
        this.timerManager.addTimerListener(new TimerListener() {
            @Override
            public void timerRunning(Timing timing) {
                if (!Main.this.timerStopped) {
                    labelTime.setText(formatTime(timing.getElapsedTime()));
                }
            }

            @Override
            public void timerStopped(Timing timing) {
                labelTime.setText(formatTime(timing.getElapsedTime()));
            }
        });
        panelTimer.add(labelTime, "3, 0");

        // labelRightHand
        final ImageIcon iconRight = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/right.png"));
        final ImageIcon iconRightPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/rightPressed.png"));

        final JLabel labelRightHand = new JLabel(iconRight);
        this.timerManager.addTimerListener(new TimerListener() {
            @Override
            public void rightHandPressed() {
                labelRightHand.setIcon(iconRightPressed);
            }

            @Override
            public void rightHandReleased() {
                labelRightHand.setIcon(iconRight);
            }
        });
        panelTimer.add(labelRightHand, "5, 0");

        return panelTimer;
    }

    private JScrollPane createTimesPanel() {
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(200, 200));
        scrollPane.setMinimumSize(new Dimension(200, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Times"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.sessionManager.addSessionListener(new SessionListener() {
            @Override
            public void solutionsUpdated(final FullSolution[] solutions) {
                JPanel panelTimes = new JPanel();
                panelTimes.setLayout(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints();
                c.ipady = 4;
                c.anchor = GridBagConstraints.BASELINE_TRAILING;

                for (int i = solutions.length - 1; i >= Math.max(0, solutions.length - 500); i--) {
                    final int index = i;

                    JLabel labelIndex = new JLabel(Integer.toString(i + 1) + ".");
                    labelIndex.setFont(new Font("Tahoma", Font.BOLD, 13));
                    c.gridx = 0;
                    c.insets = new Insets(0, 0, 0, 8);
                    panelTimes.add(labelIndex, c);

                    JLabel labelTime = new JLabel(formatTime(solutions[i].getSolution().timing.getElapsedTime()));
                    labelTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
                    c.gridx = 2;
                    c.insets = new Insets(0, 0, 0, 16);
                    panelTimes.add(labelTime, c);

                    final JLabel labelPlus2 = new JLabel("+2");
                    labelPlus2.setFont(new Font("Tahoma", Font.PLAIN, 13));
                    if (!solutions[index].getSolution().penalty.equals("+2")) {
                        labelPlus2.setForeground(Color.LIGHT_GRAY);
                    }
                    labelPlus2.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    labelPlus2.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (!solutions[index].getSolution().penalty.equals("+2")) {
                                solutions[index].getSolution().penalty = "+2";
                                Main.this.solutionManager.updateSolution(solutions[index]);
                            } else if (solutions[index].getSolution().penalty.equals("+2")) {
                                solutions[index].getSolution().penalty = "";
                                Main.this.solutionManager.updateSolution(solutions[index]);
                            }
                        }
                    });
                    c.gridx = 3;
                    c.insets = new Insets(0, 0, 0, 8);
                    panelTimes.add(labelPlus2, c);

                    final JLabel labelDNF = new JLabel("DNF");
                    labelDNF.setFont(new Font("Tahoma", Font.PLAIN, 13));
                    if (!solutions[index].getSolution().penalty.equals("DNF")) {
                        labelDNF.setForeground(Color.LIGHT_GRAY);
                    }
                    labelDNF.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    labelDNF.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (!solutions[index].getSolution().penalty.equals("DNF")) {
                                solutions[index].getSolution().penalty = "DNF";
                                Main.this.solutionManager.updateSolution(solutions[index]);
                            } else if (solutions[index].getSolution().penalty.equals("DNF")) {
                                solutions[index].getSolution().penalty = "";
                                Main.this.solutionManager.updateSolution(solutions[index]);
                            }
                        }
                    });
                    c.gridx = 4;
                    c.insets = new Insets(0, 0, 0, 16);
                    panelTimes.add(labelDNF, c);

                    JLabel labelX = new JLabel();
                    labelX.setIcon(new ImageIcon(getClass().getResource("/com/puzzletimer/resources/x.png")));
                    labelX.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    labelX.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Main.this.solutionManager.removeSolution(solutions[index]);
                        }
                    });
                    c.gridx = 5;
                    c.insets = new Insets(0, 0, 0, 0);
                    panelTimes.add(labelX, c);
                }

                scrollPane.setViewportView(panelTimes);
            }
        });

        return scrollPane;
    }

    private JPanel createStatisticsPanel() {
        // panelStatistics
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        JPanel panelStatistics = new JPanel(new TableLayout(new double[][] {
            { f, p, 8, p, f },
            { f, p, 1, p, 1, p, 6, p, 1, p, 1, p, 1, p, 6, p, 1, p, 1, p, 1, p, f },
        }));
        panelStatistics.setBorder(BorderFactory.createTitledBorder("Statistics"));

        StatisticalMeasure[] statistics = {
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

        int y = 1;
        for (final StatisticalMeasure measure : statistics) {
            JLabel labelMeasure = new JLabel(measure.getDescription() + ":");
            labelMeasure.setFont(new Font("Tahoma", Font.BOLD, 11));
            panelStatistics.add(labelMeasure, "1, " + y + ", R, C");

            final JLabel labelValue = new JLabel("XX:XX.XX");
            panelStatistics.add(labelValue, "3, " + y + ", L, C");

            this.sessionManager.addSessionListener(new SessionListener() {
                @Override
                public void solutionsUpdated(FullSolution[] solutions) {
                    if (solutions.length >= measure.getMinimumWindowSize()) {
                        int size = Math.min(solutions.length, measure.getMaximumWindowSize());

                        Solution[] window = new Solution[size];
                        for (int i = 0; i < size; i++) {
                            window[i] = solutions[solutions.length - size + i].getSolution();
                        }

                        labelValue.setText(formatTime(measure.calculate(window)));
                    } else {
                        labelValue.setText("XX:XX.XX");
                    }
                }
            });

            y += 2;
        }

        return panelStatistics;
    }

    private JPanel createScramblePanel() {
        // panelScramble
        JPanel panelScramble = new JPanel();
        panelScramble.setBorder(BorderFactory.createTitledBorder("Scramble"));
        panelScramble.setLayout(new TableLayout(new double[][] {
            { TableLayout.FILL },
            { TableLayout.PREFERRED },
        }));

        // panel3D
        final Panel3D panel3D = new Panel3D();
        panel3D.setMinimumSize(new Dimension(200, 180));
        panel3D.setPreferredSize(new Dimension(200, 180));
        panel3D.setFocusable(false);
        panelScramble.add(panel3D, "0, 0");

        this.scrambleManager.addScrambleListener(new ScrambleListener() {
            @Override
            public void scrambleChanged(String[] sequence) {
                Category currentCategory = Main.this.categoryManager.getCurrentCategory();
                Scrambler scrambler = ScramblerBuilder.getScrambler(currentCategory.getScramblerId());
                Puzzle puzzle = PuzzleBuilder.getPuzzle(scrambler.getScramblerInfo().getPuzzleId());
                panel3D.mesh = puzzle.getScrambledPuzzleMesh(sequence);
                panel3D.repaint();
            }
        });

        return panelScramble;
    }

    private String formatTime(long time) {
        if (time == Long.MAX_VALUE) {
            return "DNF";
        }

        return String.format(
            "%02d:%02d.%02d",
            time / 60000,
            (time % 60000) / 1000,
            (time % 1000) / 10);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e){
                }

                JFrame frame = new Main();
                frame.setSize(640, 480);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}

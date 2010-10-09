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
import com.puzzletimer.util.SolutionUtils;

@SuppressWarnings("serial")
public class Main extends JFrame {
    private CategoryManagerFrame categoryManagerDialog;
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
        Category[] categories = {
            new Category(null, "2x2x2-CUBE-RANDOM", "2x2x2 cube", false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube", false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube one-handed", false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube blindfolded", false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube with feet", false),
            new Category(null, "4x4x4-CUBE-RANDOM", "4x4x4 cube", false),
            new Category(null, "4x4x4-CUBE-RANDOM", "4x4x4 blindfolded", false),
            new Category(null, "5x5x5-CUBE-RANDOM", "5x5x5 cube", false),
            new Category(null, "5x5x5-CUBE-RANDOM", "5x5x5 blindfolded", false),
            new Category(null, "MEGAMINX-RANDOM", "Megaminx", false),
            new Category(null, "PYRAMINX-RANDOM", "Pyraminx", false),
            new Category(null, "SQUARE-1-RANDOM", "Square-1", false),
            new Category(null, "EMPTY", "Rubik's clock", false),
            new Category(null, "EMPTY", "Rubik's magic", false),
            new Category(null, "EMPTY", "Master magic", false),
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
        Category defaultCategory = categories[1];

        // timer manager
        this.timerManager = new TimerManager(new KeyboardTimer(this, KeyEvent.VK_CONTROL));

        // categoryManager
        this.categoryManager = new CategoryManager(categories, defaultCategory);

        // scramble manager
        this.scrambleManager = new ScrambleManager(ScramblerBuilder.getScrambler(defaultCategory.scramblerId));
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void currentCategoryChanged(Category category) {
                Scrambler scrambler = ScramblerBuilder.getScrambler(category.scramblerId);
                Main.this.scrambleManager.setScrambler(scrambler);
            }
        });

        // solution manager
        this.solutionManager = new SolutionManager();
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void currentCategoryChanged(Category category) {
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
            public void currentCategoryChanged(Category category) {
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
        this.categoryManager.setCurrentCategory(defaultCategory);
    }

    private void createComponents() {
        // frameMain
        setMinimumSize(new Dimension(800, 600));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/com/puzzletimer/resources/icon.png")));
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                setTitle("Puzzle Timer - " + currentCategory.description);
            }
        });

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

        // category manager
        this.categoryManagerDialog = new CategoryManagerFrame(this.categoryManager);

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
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                menuCategory.removeAll();

                // category manager
                JMenuItem menuItemCategoryManager = new JMenuItem("Category manager...");
                menuItemCategoryManager.setMnemonic(KeyEvent.VK_M);
                menuItemCategoryManager.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                menuItemCategoryManager.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        Main.this.categoryManagerDialog.setVisible(true);
                    }
                });
                menuCategory.add(menuItemCategoryManager);

                menuCategory.addSeparator();

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

                // TODO: find a better way to do this
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
                    new BuiltInCategory(categories[9], 'M', 'M'),
                    new BuiltInCategory(categories[10], 'P', 'P'),
                    new BuiltInCategory(categories[11], 'S', '1'),
                    new BuiltInCategory(categories[12], 'C', 'K'),
                    new BuiltInCategory(categories[13], 'M', 'G'),
                    new BuiltInCategory(categories[14], 'M', 'A'),
                };

                for (final BuiltInCategory builtInCategory : builtInCategories) {
                    JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(builtInCategory.category.description);
                    menuItemCategory.setMnemonic(builtInCategory.mnemonic);
                    if (builtInCategory.accelerator != '\0') {
                        menuItemCategory.setAccelerator(KeyStroke.getKeyStroke(builtInCategory.accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                    }
                    menuItemCategory.setSelected(builtInCategory.category == currentCategory);
                    menuItemCategory.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Main.this.categoryManager.setCurrentCategory(builtInCategory.category);
                        }
                    });
                    menuCategory.add(menuItemCategory);
                    categoryGroup.add(menuItemCategory);
                }

                menuCategory.addSeparator();

                // user defined categories
                for (final Category category : categories) {
                    if (category.isUserDefined()) {
                        JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(category.description);
                        menuItemCategory.setSelected(category == currentCategory);
                        menuItemCategory.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Main.this.categoryManager.setCurrentCategory(category);
                            }
                        });
                        menuCategory.add(menuItemCategory);
                        categoryGroup.add(menuItemCategory);
                    }
                }
            }
        });

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
                    labelTime.setText(SolutionUtils.formatMinutes(timing.getElapsedTime()));
                }
            }

            @Override
            public void timerStopped(Timing timing) {
                labelTime.setText(SolutionUtils.formatMinutes(timing.getElapsedTime()));
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

                for (int i = 0; i < Math.min(500, solutions.length); i++) {
                    final int index = i;

                    JLabel labelIndex = new JLabel(Integer.toString(solutions.length - i) + ".");
                    labelIndex.setFont(new Font("Tahoma", Font.BOLD, 13));
                    c.gridx = 0;
                    c.insets = new Insets(0, 0, 0, 8);
                    panelTimes.add(labelIndex, c);

                    JLabel labelTime = new JLabel(SolutionUtils.formatMinutes(solutions[i].getSolution().timing.getElapsedTime()));
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

                        labelValue.setText(SolutionUtils.formatMinutes(measure.calculate(window)));
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
                Scrambler scrambler = ScramblerBuilder.getScrambler(currentCategory.scramblerId);
                Puzzle puzzle = PuzzleBuilder.getPuzzle(scrambler.getScramblerInfo().getPuzzleId());
                panel3D.mesh = puzzle.getScrambledPuzzleMesh(sequence);
                panel3D.repaint();
            }
        });

        return panelScramble;
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

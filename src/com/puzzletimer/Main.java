package com.puzzletimer;

import info.clearthought.layout.TableLayout;

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
import java.util.ArrayList;
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
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.Mean;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.timer.KeyboardTimer;
import com.puzzletimer.timer.StackmatTimer;
import com.puzzletimer.timer.Timer;
import com.puzzletimer.timer.TimerListener;

@SuppressWarnings("serial")
public class Main extends JFrame implements TimerListener {
    private ImageIcon iconLeft;
    private ImageIcon iconRight;
    private ImageIcon iconLeftPressed;
    private ImageIcon iconRightPressed;

    private JLabel labelLeftHand;
    private JLabel labelTime;
    private JLabel labelRightHand;

    private boolean timerStopped;
    private Timer timer;
    private State state;

    private AudioFormat audioFormat;
    private Mixer.Info mixerInfo;

    public Main() {
        this.iconLeft = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/left.png"));
        this.iconRight = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/right.png"));
        this.iconLeftPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/leftPressed.png"));
        this.iconRightPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/rightPressed.png"));

        this.timerStopped = false;

        this.timer = new KeyboardTimer(this, KeyEvent.VK_CONTROL);
        this.timer.addEventListener(this);
        this.timer.start();

        this.state = new State(new Category(UUID.randomUUID(), "RUBIKS-CUBE-RANDOM", "Rubik's Cube", '3', '3', true));

        this.audioFormat = new AudioFormat(8000, 8, 1, true, false);
        this.mixerInfo = null;

        createComponents();

        this.state.notifyScrambleObservers();
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
        this.state.addStateObserver(new StateObserver() {
            @Override
            public void updateScramble(Puzzle puzzle, Scramble scramble) {
                panelScramble.removeAll();

                for (String move : scramble.getSequence()) {
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

        // menuCategory
        final JMenu menuCategory = new JMenu("Category");
        menuCategory.setMnemonic(KeyEvent.VK_C);
        menuBar.add(menuCategory);
        ButtonGroup categoryGroup = new ButtonGroup();

        // menuCategory items
        Category[] categories = {
            new Category(null, "2x2x2-CUBE-RANDOM", "2x2x2 cube", '2', '2', false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube", 'R', '3', true),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube one-handed", 'O', 'O', false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube blindfolded", 'B', 'B', false),
            new Category(null, "RUBIKS-CUBE-RANDOM", "Rubik's cube with feet", 'F', 'F', false),
            new Category(null, "4x4x4-CUBE-RANDOM", "4x4x4 cube", '4', '4', false),
            new Category(null, "4x4x4-CUBE-RANDOM", "4x4x4 blindfolded", 'B', '\0', false),
            new Category(null, "5x5x5-CUBE-RANDOM", "5x5x5 cube", '5', '5', false),
            new Category(null, "5x5x5-CUBE-RANDOM", "5x5x5 blindfolded", 'B', '\0', false),
            new Category(null, "MEGAMINX-RANDOM", "Megaminx", 'M', 'M', false),
            new Category(null, "PYRAMINX-RANDOM", "Pyraminx", 'P', 'P', false),
            new Category(null, "SQUARE-1-RANDOM", "Square-1", 'S', '1', false),
            new Category(null, "EMPTY", "Rubik's clock", 'C', '\0', false),
            new Category(null, "EMPTY", "Rubik's magic", 'M', '\0', false),
            new Category(null, "EMPTY", "Master magic", 'M', '\0', false),
            null, // separator
            new Category(null, "RUBIKS-CUBE-RANDOM-EDGES", "Rubik's cube - random edges", 'E', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-EDGES-PERMUTATION", "Rubik's cube - random edges permutation", 'P', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-EDGES-ORIENTATION", "Rubik's cube - random edges orientation", 'O', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-CORNERS", "Rubik's cube - random corners", 'C', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-CORNERS-PERMUTATION", "Rubik's cube - random corners permutation", 'P', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-CORNERS-ORIENTATION", "Rubik's cube - random corners orientation", 'O', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-LAST-LAYER", "Rubik's cube - random last layer", 'P', '\0', false),
            new Category(null, "RUBIKS-CUBE-RANDOM-LAST-LAYER-PERMUTATION", "Rubik's cube - random last layer permutation", 'O', '\0', false),
            new Category(null, "RUBIKS-CUBE-EASY-CROSS", "Rubik's cube - easy cross", 'E', '\0', false),
            new Category(null, "RUBIKS-CUBE-HARD-CROSS", "Rubik's cube - hard cross", 'H', '\0', false),
        };

        for (final Category category : categories) {
            if (category == null) {
                menuCategory.addSeparator();
                continue;
            }

            final JRadioButtonMenuItem menuItemCategory = new JRadioButtonMenuItem(category.getDescription());
            menuItemCategory.setMnemonic(category.getMnemonic());
            if (category.getAccelerator() != '\0') {
                menuItemCategory.setAccelerator(KeyStroke.getKeyStroke(category.getAccelerator(), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            }
            menuItemCategory.setSelected(category.isDefault());
            menuItemCategory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.this.state.setCategory(category);
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
                Main.this.timer.removeEventListener(Main.this);
                Main.this.timer.stop();

                Main.this.timerStopped = false;

                Main.this.timer = new KeyboardTimer(Main.this, KeyEvent.VK_CONTROL);
                Main.this.timer.addEventListener(Main.this);
                Main.this.timer.start();
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
                Main.this.timer.removeEventListener(Main.this);
                Main.this.timer.stop();

                Main.this.timerStopped = false;

                Main.this.timer = new KeyboardTimer(Main.this, KeyEvent.VK_SPACE);
                Main.this.timer.addEventListener(Main.this);
                Main.this.timer.start();
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
                Main.this.timer.removeEventListener(Main.this);
                Main.this.timer.stop();

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
                    Main.this.timer = new KeyboardTimer(Main.this, KeyEvent.VK_CONTROL);
                    Main.this.timer.addEventListener(Main.this);
                    Main.this.timer.start();
                    return;
                }

                Main.this.timer = new StackmatTimer(targetDataLine);
                Main.this.timer.addEventListener(Main.this);
                Main.this.timer.start();
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
                JOptionPane.showMessageDialog(frame, "Puzzle Timer 0.1\r\n2010\r\nhttp://www.puzzletimer.com\r\nWalter Souza <walterprs@gmail.com>", "About", JOptionPane.INFORMATION_MESSAGE);
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
        this.labelLeftHand = new JLabel(this.iconLeft);
        panelTimer.add(this.labelLeftHand, "1, 0");

        // labelTime
        this.labelTime = new JLabel("00:00.00");
        this.labelTime.setFont(new Font("Arial", Font.BOLD, 108));
        panelTimer.add(this.labelTime, "3, 0");

        // labelRightHand
        this.labelRightHand = new JLabel(this.iconRight);
        panelTimer.add(this.labelRightHand, "5, 0");

        return panelTimer;
    }

    private JScrollPane createTimesPanel() {
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(200, 200));
        scrollPane.setMinimumSize(new Dimension(200, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Times"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.state.addStateObserver(new StateObserver() {
            @Override
            public void updateSolutions(ArrayList<Solution> solutions) {
                JPanel panelTimes = new JPanel();
                panelTimes.setLayout(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints();
                c.ipady = 4;
                c.anchor = GridBagConstraints.BASELINE_TRAILING;

                for (int i = solutions.size() - 1; i >= Math.max(0, solutions.size() - 100); i--) {
                    JLabel labelIndex = new JLabel(Integer.toString(i + 1) + ".");
                    labelIndex.setFont(new Font("Tahoma", Font.BOLD, 13));
                    c.gridx = 0;
                    c.insets = new Insets(0, 0, 0, 8);
                    panelTimes.add(labelIndex, c);

                    JLabel labelTime = new JLabel(formatTime(solutions.get(i).getTiming().getElapsedTime()));
                    labelTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
                    c.gridx = 2;
                    c.insets = new Insets(0, 0, 0, 16);
                    panelTimes.add(labelTime, c);

                    final int index = i;
                    JLabel labelX = new JLabel();
                    labelX.setIcon(new ImageIcon(getClass().getResource("/com/puzzletimer/resources/x.png")));
                    labelX.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    labelX.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Main.this.state.removeSolution(index);
                        }
                    });
                    c.gridx = 3;
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

            this.state.addStateObserver(new StateObserver() {
                @Override
                public void updateSolutions(ArrayList<Solution> solutions) {
                    if (solutions.size() >= measure.getMinimumWindowSize()) {
                        int size = Math.min(solutions.size(), measure.getMaximumWindowSize());

                        Solution[] window = new Solution[size];
                        for (int i = 0; i < size; i++) {
                            window[i] = solutions.get(solutions.size() - size + i);
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

        this.state.addStateObserver(new StateObserver() {
            @Override
            public void updateScramble(Puzzle puzzle, Scramble scramble) {
                panel3D.mesh = puzzle.getScrambledPuzzleMesh(scramble);
                panel3D.repaint();
            }
        });

        return panelScramble;
    }

    private String formatTime(long time) {
        long minutes = time / 60000;
        long seconds = (time % 60000) / 1000;
        long centiseconds = (time % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds);
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

    @Override
    public void leftHandPressed() {
        this.labelLeftHand.setIcon(this.iconLeftPressed);
    }

    @Override
    public void leftHandReleased() {
        this.labelLeftHand.setIcon(this.iconLeft);
    }

    @Override
    public void rightHandPressed() {
        this.labelRightHand.setIcon(this.iconRightPressed);
    }

    @Override
    public void rightHandReleased() {
        this.labelRightHand.setIcon(this.iconRight);
    }

    @Override
    public void timerReady() {
        this.timerStopped = false;
    }

    @Override
    public void timerRunning(Timing timing) {
        if (!this.timerStopped) {
            this.labelTime.setText(formatTime(timing.getElapsedTime()));
        }
    }

    @Override
    public void timerStopped(Timing timing) {
        this.labelTime.setText(formatTime(timing.getElapsedTime()));

        if (!this.timerStopped) {
            this.state.addSolution(timing);
        }

        this.timerStopped = true;
    }
}

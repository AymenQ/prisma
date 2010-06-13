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
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.TrimmedAverage;
import com.puzzletimer.timer.KeyboardTimer;
import com.puzzletimer.timer.StackmatTimer;
import com.puzzletimer.timer.Timer;
import com.puzzletimer.timer.TimerListener;
import com.puzzletimer.timer.Timing;

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

    public Main() {
        this.iconLeft = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/left.png"));
        this.iconRight = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/right.png"));
        this.iconLeftPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/leftPressed.png"));
        this.iconRightPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/rightPressed.png"));

        this.timerStopped = false;

        this.timer = new KeyboardTimer(this, KeyEvent.VK_CONTROL);
        this.timer.addEventListener(this);
        this.timer.start();

        this.state = new State(new RubiksCube());

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

                for (Move m : scramble.moves) {
                    JLabel label = new JLabel(m.toString());
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

        // menuPuzzle
        final JMenu menuPuzzle = new JMenu("Puzzle");
        menuPuzzle.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menuPuzzle);
        ButtonGroup puzzleGroup = new ButtonGroup();

        // menuPuzzle items
        Puzzle[] puzzles = {
            new RubiksPocketCube(),
            new RubiksCube(),
            new RubiksRevenge(),
            new ProfessorsCube(),
            new Megaminx(),
            new Pyraminx(),
            new Square1(),
        };

        for (final Puzzle puzzle : puzzles) {
            final JRadioButtonMenuItem menuItemPuzzle = new JRadioButtonMenuItem(puzzle.getName());
            menuItemPuzzle.setMnemonic(puzzle.getMnemonic());
            menuItemPuzzle.setAccelerator(puzzle.getAccelerator());
            menuItemPuzzle.setSelected(puzzle.isDefaultPuzzle());
            menuItemPuzzle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.this.state.setPuzzle(puzzle);
                }
            });
            menuPuzzle.add(menuItemPuzzle);
            puzzleGroup.add(menuItemPuzzle);
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

                Main.this.timer = new StackmatTimer();
                Main.this.timer.addEventListener(Main.this);
                Main.this.timer.start();
            }
        });
        menuTimerTrigger.add(menuItemStackmatTimer);
        timerTriggerGroup.add(menuItemStackmatTimer);

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

        // labelAverage
        JLabel labelAverage = new JLabel("Average:");
        labelAverage.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelAverage, "1, 1, R, C");

        // labelAverageValue
        final JLabel labelAverageValue = new JLabel("XX:XX.XX");
        panelStatistics.add(labelAverageValue, "3, 1, L, C");

        // labelStandardDeviation
        JLabel labelStandardDeviation = new JLabel("Standard Deviation:");
        labelStandardDeviation.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelStandardDeviation, "1, 3, R, C");

        // labelStandardDeviationValue
        final JLabel labelStandardDeviationValue = new JLabel("XX:XX.XX");
        panelStatistics.add(labelStandardDeviationValue, "3, 3, L, C");

        // labelBestTime
        JLabel labelBestTime = new JLabel("Best Time:");
        labelBestTime.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelBestTime, "1, 5, R, C");

        // labelBestTimeValue
        final JLabel labelBestTimeValue = new JLabel("XX:XX.XX");
        panelStatistics.add(labelBestTimeValue, "3, 5, L, C");

        // labelAverageLast3
        JLabel labelAverageLast3 = new JLabel("Average (Last 3):");
        labelAverageLast3.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelAverageLast3, "1, 7, R, C");

        // labelAverageLast3Value
        final JLabel labelAverageLast3Value = new JLabel("XX:XX.XX");
        labelAverageLast3Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelAverageLast3Value, "3, 7, L, C");

        // labelTrimmedAverageLast5
        JLabel labelTrimmedAverageLast5 = new JLabel("Trimmed Average (Last 5):");
        labelTrimmedAverageLast5.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelTrimmedAverageLast5, "1, 9, R, C");

        // labelTrimmedAverageLast5Value
        final JLabel labelTrimmedAverageLast5Value = new JLabel("XX:XX.XX");
        labelTrimmedAverageLast5Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelTrimmedAverageLast5Value, "3, 9, L, C");

        // labelStandardDeviationLast3
        JLabel labelStandardDeviationLast3 = new JLabel("Standard Deviation (Last 3):");
        labelStandardDeviationLast3.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelStandardDeviationLast3, "1, 11, R, C");

        // labelStandardDeviationLast3Value
        final JLabel labelStandardDeviationLast3Value = new JLabel("XX:XX.XX");
        labelStandardDeviationLast3Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelStandardDeviationLast3Value, "3, 11, L, C");

        // labelBestTimeLast3
        JLabel labelBestTimeLast3 = new JLabel("Best Time (Last 3):");
        labelBestTimeLast3.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelBestTimeLast3, "1, 13, R, C");

        // labelBestTimeLast3Value
        final JLabel labelBestTimeLast3Value = new JLabel("XX:XX.XX");
        labelBestTimeLast3Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelBestTimeLast3Value, "3, 13, L, C");

        // labelAverageLast10
        JLabel labelAverageLast10 = new JLabel("Average (Last 10):");
        labelAverageLast10.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelAverageLast10, "1, 15, R, C");

        // labelAverageLast10Value
        final JLabel labelAverageLast10Value = new JLabel("XX:XX.XX");
        labelAverageLast10Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelAverageLast10Value, "3, 15, L, C");

        // labelTrimmedAverageLast12
        JLabel labelTrimmedAverageLast12 = new JLabel("Trimmed Average (Last 12):");
        labelTrimmedAverageLast12.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelTrimmedAverageLast12, "1, 17, R, C");

        // labelTrimmedAverageLast12Value
        final JLabel labelTrimmedAverageLast12Value = new JLabel("XX:XX.XX");
        labelTrimmedAverageLast12Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelTrimmedAverageLast12Value, "3, 17, L, C");

        // labelStandardDeviationLast10
        JLabel labelStandardDeviationLast10 = new JLabel("Standard Deviation (Last 10):");
        labelStandardDeviationLast10.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelStandardDeviationLast10, "1, 19, R, C");

        // labelStandardDeviationLast10Value
        final JLabel labelStandardDeviationLast10Value = new JLabel("XX:XX.XX");
        labelStandardDeviationLast10Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelStandardDeviationLast10Value, "3, 19, L, C");

        // labelBestTimeLast10
        JLabel labelBestTimeLast10 = new JLabel("Best Time (Last 10):");
        labelBestTimeLast10.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelStatistics.add(labelBestTimeLast10, "1, 21, R, C");

        // labelBestTimeLast10Value
        final JLabel labelBestTimeLast10Value = new JLabel("XX:XX.XX");
        labelBestTimeLast10Value.setFont(new Font("Tahoma", Font.PLAIN, 11));
        panelStatistics.add(labelBestTimeLast10Value, "3, 21, L, C");

        this.state.addStateObserver(new StateObserver() {
            @Override
            public void updateSolutions(ArrayList<Solution> solutions) {
                if (solutions.size() >= 1) {
                    labelAverageValue.setText(formatTime(new Average().getValue(solutions)));
                    labelStandardDeviationValue.setText(formatTime(new StandardDeviation().getValue(solutions)));
                    labelBestTimeValue.setText(formatTime(new Best().getValue(solutions)));
                } else {
                    labelAverageValue.setText("XX:XX.XX");
                    labelStandardDeviationValue.setText("XX:XX.XX");
                    labelBestTimeValue.setText("XX:XX.XX");
                }

                if (solutions.size() >= 3) {
                    ArrayList<Solution> last3Solutions = new ArrayList<Solution>(solutions.subList(solutions.size() - 3, solutions.size()));
                    labelAverageLast3Value.setText(formatTime(new Average().getValue(last3Solutions)));
                    labelStandardDeviationLast3Value.setText(formatTime(new StandardDeviation().getValue(last3Solutions)));
                    labelBestTimeLast3Value.setText(formatTime(new Best().getValue(last3Solutions)));
                } else {
                    labelAverageLast3Value.setText("XX:XX.XX");
                    labelStandardDeviationLast3Value.setText("XX:XX.XX");
                    labelBestTimeLast3Value.setText("XX:XX.XX");
                }

                if (solutions.size() >= 5) {
                    ArrayList<Solution> last5Solutions = new ArrayList<Solution>(solutions.subList(solutions.size() - 5, solutions.size()));
                    labelTrimmedAverageLast5Value.setText((formatTime(new TrimmedAverage().getValue(last5Solutions))));
                } else {
                    labelTrimmedAverageLast5Value.setText("XX:XX.XX");
                }

                if (solutions.size() >= 10) {
                    ArrayList<Solution> last10Solutions = new ArrayList<Solution>(solutions.subList(solutions.size() - 10, solutions.size()));
                    labelAverageLast10Value.setText(formatTime(new Average().getValue(last10Solutions)));
                    labelStandardDeviationLast10Value.setText(formatTime(new StandardDeviation().getValue(last10Solutions)));
                    labelBestTimeLast10Value.setText(formatTime(new Best().getValue(last10Solutions)));
                } else {
                    labelAverageLast10Value.setText("XX:XX.XX");
                    labelStandardDeviationLast10Value.setText("XX:XX.XX");
                    labelBestTimeLast10Value.setText("XX:XX.XX");
                }

                if (solutions.size() >= 12) {
                    ArrayList<Solution> last12Solutions = new ArrayList<Solution>(solutions.subList(solutions.size() - 12, solutions.size()));
                    labelTrimmedAverageLast12Value.setText((formatTime(new TrimmedAverage().getValue(last12Solutions))));
                } else {
                    labelTrimmedAverageLast12Value.setText("XX:XX.XX");
                }
            }
        });

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
                panel3D.mesh = puzzle.getMesh(scramble);
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

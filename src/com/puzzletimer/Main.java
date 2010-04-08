package com.puzzletimer;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.puzzletimer.graphics.Panel3D;
import com.puzzletimer.scrambles.RubiksCubeRandomScrambler;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.statistics.Average;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.StandardDeviation;
import com.puzzletimer.statistics.TrimmedAverage;
import com.puzzletimer.timer.TimerController;
import com.puzzletimer.timer.TimerControllerEvent;
import com.puzzletimer.timer.TimerControllerListener;

@SuppressWarnings("serial")
public class Main extends JFrame {
    private State state;
	private JLabel labelTime;
	
	public Main() {
		state = new State(new RubiksCubeRandomScrambler(25));
		
		createComponents();

		// timerController
        final TimerController timerController = new TimerController();
        timerController.addEventListener(new TimerControllerListener() {
        	@Override
            public void timerStarted(TimerControllerEvent event) {
        		state.startCurrentSolution();
            }
        	
        	@Override
            public void timerStopped(TimerControllerEvent event) {
        		state.stopCurrentSolution();
            }
        });

        addKeyListener(new KeyAdapter() {
        	@Override
			public void keyPressed(KeyEvent event) {
        		if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
        			if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
        				timerController.pressLeftButton();
        			} else {
        				timerController.pressRightButton();
        			}
                }        		
        	}
        	
            @Override
			public void keyReleased(KeyEvent event) {
            	if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
            		if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
            			timerController.releaseLeftButton();	
            		} else {
            			timerController.releaseRightButton();
            		}
                }       	
            }
        });
        
        state.addStateObserver(new StateObserver() {
            private java.util.Timer repeater;
            
        	public void onSolutionBegin(final Solution solution) {
                repeater = new java.util.Timer();
                repeater.schedule(new java.util.TimerTask() {
                    @Override
        			public void run() {
                    	labelTime.setText(formatTime(solution.getTimer().getDiff()));
                    }
                }, 0, 5);
        	}
        	
        	public void onSolutionEnd(Solution solution) {
                repeater.cancel();
                labelTime.setText(formatTime(solution.getTimer().getDiff()));
        	}
        });
        
        state.notifyScrambleObservers();
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
			{ 20, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, 3 },
		}));
		add(panelMain);

		// labelScramble
		final JLabel labelScramble = new JLabel();
		labelScramble.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelScramble.setFont(new Font("Arial", Font.PLAIN, 18));
		state.addStateObserver(new StateObserver() {
			@Override
			public void updateScramble(Scramble scramble) {
				labelScramble.setText(scramble.toString());
			}			
		});
		panelMain.add(labelScramble, "1, 1, 3, 1, C, C");

		// timer panel
		panelMain.add(createTimerPanel(), "1, 3, 3, 3");
		
		// times panel
		panelMain.add(createTimesPanel(), "1, 5");

		// statistics panel
		panelMain.add(createStatisticsPanel(), "2, 5");

		// scramble panel
		panelMain.add(createScramblePanel(), "3, 5");
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
		
		// menuItemRubiksCube
		final JMenuItem menuItemRubiksCube = new JMenuItem("Rubik's Cube");
		menuItemRubiksCube.setMnemonic(KeyEvent.VK_R);
		menuItemRubiksCube.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menuPuzzle.add(menuItemRubiksCube);		
		
		// menuItemPyraminx
		final JMenuItem menuItemPyraminx = new JMenuItem("Pyraminx");
		menuItemPyraminx.setMnemonic(KeyEvent.VK_P);
		menuItemPyraminx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menuPuzzle.add(menuItemPyraminx);		
		
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
				JOptionPane.showMessageDialog(frame, "Puzzle Timer 0.0\r\n2010\r\nWalter Souza <walterprs@gmail.com>", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuHelp.add(menuItemAbout);
		
		return menuBar;
	}
	
	private JPanel createTimerPanel() {
	    final ImageIcon iconLeft = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/left.png"));
	    final ImageIcon iconRight = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/right.png"));
	    final ImageIcon iconLeftPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/leftPressed.png"));
	    final ImageIcon iconRightPressed = new ImageIcon(getClass().getResource("/com/puzzletimer/resources/rightPressed.png"));
		
		// panelTime
		JPanel panelTimer = new JPanel(new TableLayout(new double[][] {
			{ 0.35, TableLayout.PREFERRED, 0.15, TableLayout.PREFERRED, 0.15, TableLayout.PREFERRED, 0.35 },
			{ TableLayout.FILL },
		}));
		
		// labelLeftHand
		final JLabel labelLeftHand = new JLabel(iconLeft);
		panelTimer.add(labelLeftHand, "1, 0");

		// labelTime
		labelTime = new JLabel("00:00.00");
		labelTime.setFont(new Font("Arial", Font.BOLD, 108));
		panelTimer.add(labelTime, "3, 0");

		// labelRightHand
		final JLabel labelRightHand = new JLabel(iconRight);
		panelTimer.add(labelRightHand, "5, 0");

        addKeyListener(new KeyAdapter() {
        	@Override
			public void keyPressed(KeyEvent event) {
        		if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
        			if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
        				labelLeftHand.setIcon(iconLeftPressed);
        			} else {
        				labelRightHand.setIcon(iconRightPressed);
        			}
                }        		
        	}
        	
            @Override
			public void keyReleased(KeyEvent event) {
            	if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
            		if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
            			labelLeftHand.setIcon(iconLeft);
            		} else {
            			labelRightHand.setIcon(iconRight);
            		}
            	}
            }
        });

		return panelTimer;
	}
	
	private JScrollPane createTimesPanel() {
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 200));
		scrollPane.setMinimumSize(new Dimension(200, 200));
		scrollPane.setBorder(BorderFactory.createTitledBorder("Times"));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		state.addStateObserver(new StateObserver() {
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
			
					JLabel labelTime = new JLabel(formatTime(solutions.get(i).getTimer().getDiff()));
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
							state.removeSolution(index);
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

		state.addStateObserver(new StateObserver() {
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
		
		state.addStateObserver(new StateObserver() {
			@Override
			public void updateScramble(Scramble scramble) {
				panel3D.mesh = RubiksCube.getMesh(scramble);
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
}

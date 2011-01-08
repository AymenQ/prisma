package com.puzzletimer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.graphics.Panel3D;
import com.puzzletimer.graphics.algebra.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.ColorScheme.FaceColor;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleProvider;
import com.puzzletimer.state.ColorListener;
import com.puzzletimer.state.ColorManager;

@SuppressWarnings("serial")
public class ColorSchemeFrame extends JFrame {
    private JComboBox comboBoxPuzzle;
    private Panel3D panel3D;
    private JTable table;
    private JButton buttonEdit;
    private JButton buttonDefault;
    private JButton buttonOk;

    public ColorSchemeFrame(final PuzzleProvider puzzleProvider, final ColorManager colorManager) {
        super();

        setMinimumSize(new Dimension(480, 600));
        setPreferredSize(getMinimumSize());

        setTitle("Color Scheme");

        createComponents();

        // combo box
        Puzzle defaultPuzzle = null;
        for (Puzzle puzzle : puzzleProvider.getAll()) {
            if (puzzle.getPuzzleInfo().getPuzzleId().equals("RUBIKS-CUBE")) {
                defaultPuzzle = puzzle;
            }

            this.comboBoxPuzzle.addItem(puzzle);
        }

        this.comboBoxPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Puzzle puzzle =
                    (Puzzle) ColorSchemeFrame.this.comboBoxPuzzle.getSelectedItem();
                ColorScheme colorScheme =
                    colorManager.getColorScheme(puzzle.getPuzzleInfo().getPuzzleId());

                update(puzzle, colorScheme);
            }
        });
        this.comboBoxPuzzle.setSelectedItem(defaultPuzzle);

        // editing buttons
        this.buttonEdit.setEnabled(false);
        this.buttonDefault.setEnabled(false);
        this.table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    int nSelected = ColorSchemeFrame.this.table.getSelectedRowCount();
                    ColorSchemeFrame.this.buttonEdit.setEnabled(nSelected == 1);
                    ColorSchemeFrame.this.buttonDefault.setEnabled(nSelected > 0);
                }
            });

        this.buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Puzzle puzzle =
                    (Puzzle) ColorSchemeFrame.this.comboBoxPuzzle.getSelectedItem();
                ColorScheme colorScheme =
                    colorManager.getColorScheme(puzzle.getPuzzleInfo().getPuzzleId());
                FaceColor faceColor =
                    colorScheme.getFaceColors()[ColorSchemeFrame.this.table.getSelectedRow()];

                Color color = JColorChooser.showDialog(
                    ColorSchemeFrame.this,
                    faceColor.getFaceDescription() + " Color",
                    faceColor.getColor());
                if (color != null) {
                    faceColor.setColor(color);
                    colorManager.setColorScheme(colorScheme);
                }
            }
        });

        this.buttonDefault.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Puzzle puzzle =
                    (Puzzle) ColorSchemeFrame.this.comboBoxPuzzle.getSelectedItem();
                ColorScheme colorScheme =
                    colorManager.getColorScheme(puzzle.getPuzzleInfo().getPuzzleId());

                for (int index : ColorSchemeFrame.this.table.getSelectedRows()) {
                    FaceColor faceColor = colorScheme.getFaceColors()[index];
                    faceColor.setColor(faceColor.getDefaultColor());
                }

                colorManager.setColorScheme(colorScheme);
            }
        });

        // ok button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                ColorSchemeFrame.this.setVisible(false);
            }
        });

        // update on colors updated events
        colorManager.addColorListener(new ColorListener() {
            @Override
            public void colorSchemeUpdated(ColorScheme colorScheme) {
                Puzzle puzzle =
                    (Puzzle) ColorSchemeFrame.this.comboBoxPuzzle.getSelectedItem();

                if (puzzle.getPuzzleInfo().getPuzzleId().equals(colorScheme.getPuzzleId())) {
                    update(puzzle, colorScheme);
                }
            }
        });

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    ColorSchemeFrame.this.setVisible(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "[grow][pref!]",
                "[pref!][pref!][pref!]12[pref!][]16[pref!]"));

        // labelPuzzle
        add(new JLabel("Puzzle"), "growx, span, wrap");

        // comboBoxPuzzle
        this.comboBoxPuzzle = new JComboBox();
        add(this.comboBoxPuzzle, "growx, span, wrap");

        // panel3D
        this.panel3D = new Panel3D();
        this.panel3D.setMinimumSize(new Dimension(300, 300));
        this.panel3D.setPreferredSize(this.panel3D.getMinimumSize());
        this.panel3D.cameraPosition = new Vector3(0d, 0d, -2d);
        add(this.panel3D, "growx, span, wrap");

        // labelColors
        add(new JLabel("Colors"), "growx, span, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);
        //this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        add(scrollPane, "grow");

        // buttonEdit
        this.buttonEdit = new JButton("Edit...");
        add(this.buttonEdit, "growx, top, split, flowy");

        // buttonDefault
        this.buttonDefault = new JButton("Default");
        add(this.buttonDefault, "growx, top, wrap");

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "tag ok, span");
    }

    private class ColorRenderer extends JLabel implements TableCellRenderer {
        public ColorRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
            // foreground
            setBackground((Color) color);

            // background
            Color backgroundColor = isSelected ? table.getSelectionBackground() : table.getBackground();
            setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, backgroundColor));

            return this;
        }
    }

    private void update(Puzzle puzzle, ColorScheme colorScheme) {
        // puzzle viewer
        this.panel3D.mesh = puzzle.getScrambledPuzzleMesh(colorScheme, new String[] { });
        this.panel3D.repaint();

        // color table
        this.table.setDefaultRenderer(Color.class, new ColorRenderer());

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String column : new String[] { "Face", "Color" }) {
            tableModel.addColumn(column);
        }

        for (FaceColor faceColor : colorScheme.getFaceColors()) {
            tableModel.addRow(new Object[] {
                faceColor.getFaceDescription(),
                faceColor.getColor(),
            });
        }

        this.table.setModel(tableModel);
    }
}

package com.puzzletimer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.parsers.ScrambleParser;
import com.puzzletimer.parsers.ScrambleParserBuilder;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerBuilder;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ScrambleListener;
import com.puzzletimer.state.ScrambleManager;

@SuppressWarnings("serial")
public class ScrambleQueueFrame extends JFrame {
    private JTable table;
    private JButton buttonUp;
    private JButton buttonDown;
    private JButton buttonRemove;
    private JButton buttonImportFromFile;
    private JButton buttonExport;
    private JComboBox comboBoxScrambler;
    private JSpinner spinnerNumberOfScrambles;
    private JButton buttonImportFromScrambler;
    private JButton buttonOk;

    public ScrambleQueueFrame(final CategoryManager categoryManager, final ScrambleManager scrambleManager) {
        super();

        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(getMinimumSize());

        createComponents();

        // on category change
        categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                // title
                setTitle("Scramble Queue - " + currentCategory.description);

                // scrambler combobox
                ScrambleQueueFrame.this.comboBoxScrambler.removeAllItems();

                Scrambler currentScrambler = ScramblerBuilder.getScrambler(
                    categoryManager.getCurrentCategory().scramblerId);
                String puzzleId = currentScrambler.getScramblerInfo().getPuzzleId();

                for (Scrambler scrambler : ScramblerBuilder.getScramblers()) {
                    if (scrambler.getScramblerInfo().getPuzzleId().equals(puzzleId)) {
                        ScrambleQueueFrame.this.comboBoxScrambler.addItem(scrambler);
                    }
                }
            }
        });
        categoryManager.notifyListeners();

        // on queue update
        scrambleManager.addScrambleListener(new ScrambleListener() {
            @Override
            public void scrambleQueueUpdated(Scramble[] queue) {
                updateTable(queue);
                updateButtons(ScrambleQueueFrame.this.table);
            }
        });

        // enable/disable buttons
        this.table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    updateButtons(ScrambleQueueFrame.this.table);
                }
            });

        // up button
        this.buttonUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JTable table = ScrambleQueueFrame.this.table;
                int[] selectedRows = table.getSelectedRows();

                // move scrambles
                scrambleManager.moveScramblesUp(selectedRows);

                // fix selection
                table.removeRowSelectionInterval(0, selectedRows.length - 1);
                for (int selectedRow : selectedRows) {
                    table.addRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                }
            }
        });

        // down button
        this.buttonDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JTable table = ScrambleQueueFrame.this.table;
                int[] selectedRows = table.getSelectedRows();

                // move scrambles
                scrambleManager.moveScramblesDown(selectedRows);

                // fix selection
                table.removeRowSelectionInterval(0, selectedRows.length - 1);
                for (int selectedRow : selectedRows) {
                    table.addRowSelectionInterval(selectedRow + 1, selectedRow + 1);
                }
            }
        });

        // remove button
        this.buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                scrambleManager.removeScrambles(
                    ScrambleQueueFrame.this.table.getSelectedRows());
            }
        });

        // import from file
        this.buttonImportFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                int action = fileChooser.showOpenDialog(ScrambleQueueFrame.this);
                if (action != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                Category category = categoryManager.getCurrentCategory();
                Scrambler scrambler = ScramblerBuilder.getScrambler(category.scramblerId);
                String puzzleId = scrambler.getScramblerInfo().getPuzzleId();
                ScrambleParser scrambleParser = ScrambleParserBuilder.getScrambleParser(puzzleId);

                Scramble[] scrambles;
                try {
                    scrambles = loadScramblesFromFile(
                        fileChooser.getSelectedFile(),
                        scrambleParser);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        ScrambleQueueFrame.this,
                        "Error opening file \"" + fileChooser.getSelectedFile().getAbsolutePath() + "\".",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                scrambleManager.addScrambles(scrambles);
            }
        });

        // export to file
        this.buttonExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                int action = fileChooser.showSaveDialog(ScrambleQueueFrame.this);
                if (action != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                Scramble[] scrambles = scrambleManager.getQueue();
                Scramble[] selectedScrambles;

                int[] selectedRows = ScrambleQueueFrame.this.table.getSelectedRows();
                if (selectedRows.length <= 0) {
                    selectedScrambles = scrambles;
                } else {
                    selectedScrambles = new Scramble[selectedRows.length];
                    for (int i = 0; i < selectedScrambles.length; i++) {
                        selectedScrambles[i] = scrambles[selectedRows[i]];
                    }
                }

                try {
                    saveScramblesToFile(
                        selectedScrambles,
                        fileChooser.getSelectedFile());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        ScrambleQueueFrame.this,
                        "Error opening file \"" + fileChooser.getSelectedFile().getAbsolutePath() + "\".",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // import from scrambler
        this.buttonImportFromScrambler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Scrambler scrambler =
                    (Scrambler) ScrambleQueueFrame.this.comboBoxScrambler.getSelectedItem();

                Scramble[] scrambles = new Scramble[
                    (Integer) ScrambleQueueFrame.this.spinnerNumberOfScrambles.getValue()];
                for (int i = 0; i < scrambles.length; i++) {
                    scrambles[i] = scrambler.getNextScramble();
                }

                scrambleManager.addScrambles(scrambles);
            }
        });

        // ok button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                ScrambleQueueFrame.this.setVisible(false);
            }
        });
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "[fill][pref!]",
                "[pref!][]12[pref!][pref!]16[pref!]"));

        // labelQueue
        add(new JLabel("Queue"), "span, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        add(scrollPane, "grow");

        // buttonUp
        this.buttonUp = new JButton("Up");
        this.buttonUp.setEnabled(false);
        add(this.buttonUp, "top, growx, split 5, flowy");

        // buttonDown
        this.buttonDown = new JButton("Down");
        this.buttonDown.setEnabled(false);
        add(this.buttonDown, "top, growx");

        // buttonRemove
        this.buttonRemove = new JButton("Remove");
        this.buttonRemove.setEnabled(false);
        add(this.buttonRemove, "top, growx");

        // buttonImportFromFile
        this.buttonImportFromFile = new JButton("Import...");
        add(this.buttonImportFromFile, "gaptop 20, top, growx");

        // buttonExport
        this.buttonExport = new JButton("Export...");
        this.buttonExport.setEnabled(false);
        add(this.buttonExport, "top, growx, wrap");

        // labelImportFromScrambler
        add(new JLabel("Import from scrambler"), "span, wrap");

        // comboBoxScrambler
        this.comboBoxScrambler = new JComboBox();
        add(this.comboBoxScrambler, "growx, span, split 3");

        // spinnerNumberOfScrambles
        this.spinnerNumberOfScrambles = new JSpinner(new SpinnerNumberModel(12, 1, 1000, 1));
        add(this.spinnerNumberOfScrambles, "");

        // buttonImportFromScrambler
        this.buttonImportFromScrambler = new JButton("Import");
        add(this.buttonImportFromScrambler, "wrap");

        // buttonOK
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "width 100!, span, right");
    }

    private void updateTable(Scramble[] queue) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String column : new String[] { "#", "Scramble" }) {
            tableModel.addColumn(column);
        }

        this.table.setModel(tableModel);

        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        TableColumn indexColumn = this.table.getColumnModel().getColumn(0);
        indexColumn.setPreferredWidth(50);

        TableColumn scrambleColumn = this.table.getColumnModel().getColumn(1);
        scrambleColumn.setPreferredWidth(1000);

        for (int i = 0; i < queue.length; i++) {
            StringBuilder sequence = new StringBuilder();
            for (String move : queue[i].getSequence()) {
                sequence.append(move + " ");
            }

            tableModel.addRow(new Object[] {
                i + 1,
                sequence.toString().trim(),
            });
        }
    }

    private void updateButtons(JTable table) {
        int[] selectedRows = table.getSelectedRows();
        int nRows = table.getRowCount();

        // up button
        this.buttonUp.setEnabled(
            selectedRows.length > 0 &&
            selectedRows[0] != 0);

        // down button
        this.buttonDown.setEnabled(
            selectedRows.length > 0 &&
            selectedRows[selectedRows.length - 1] != nRows - 1);

        // remove button
        this.buttonRemove.setEnabled(selectedRows.length > 0);

        // export button
        this.buttonExport.setEnabled(nRows > 0);
    }

    private Scramble[] loadScramblesFromFile(File file, ScrambleParser scrambleParser) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        Scanner scanner = new Scanner(fileInputStream, "UTF-8");

        ArrayList<Scramble> scrambles = new ArrayList<Scramble>();
        while (scanner.hasNextLine()) {
            scrambles.add(
                new Scramble(
                    UUID.randomUUID(),
                    "",
                    scrambleParser.parse(scanner.nextLine().trim())));
        }

        scanner.close();

        Scramble[] scrambleArray = new Scramble[scrambles.size()];
        scrambles.toArray(scrambleArray);
        return scrambleArray;
    }

    private void saveScramblesToFile(Scramble[] scrambles, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "UTF-8");

        for (Scramble scramble : scrambles) {
            StringBuilder sequence = new StringBuilder();
            for (String move : scramble.getSequence()) {
                sequence.append(move + " ");
            }

            writer.append(sequence.toString().trim());
            writer.append("\r\n");
        }

        writer.close();
    }
}
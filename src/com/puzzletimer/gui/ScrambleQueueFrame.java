package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.parsers.ScrambleParser;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryManager;
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

    public ScrambleQueueFrame(
            final ScrambleParserProvider scrambleParserProvider,
            final ScramblerProvider scramblerProvider,
            final CategoryManager categoryManager,
            final ScrambleManager scrambleManager) {
        super();

        setMinimumSize(new Dimension(640, 480));

        createComponents();
        pack();

        // on category change
        categoryManager.addListener(new CategoryManager.Listener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                // title
                setTitle(
                    String.format(
                        _("scramble_queue.scramble_queue-category"),
                        currentCategory.getDescription()));

                // scrambler combobox
                ScrambleQueueFrame.this.comboBoxScrambler.removeAllItems();

                Scrambler currentScrambler = scramblerProvider.get(
                    categoryManager.getCurrentCategory().getScramblerId());
                String puzzleId = currentScrambler.getScramblerInfo().getPuzzleId();

                for (Scrambler scrambler : scramblerProvider.getAll()) {
                    if (scrambler.getScramblerInfo().getPuzzleId().equals(puzzleId)) {
                        ScrambleQueueFrame.this.comboBoxScrambler.addItem(scrambler);
                    }
                }
            }
        });
        categoryManager.notifyListeners();

        // on queue update
        scrambleManager.addListener(new ScrambleManager.Listener() {
            @Override
            public void scrambleQueueUpdated(Scramble[] queue) {
                updateTable(queue);
                updateButtons(ScrambleQueueFrame.this.table);
            }
        });

        // enable/disable buttons
        this.table.getSelectionModel().addListSelectionListener(
                event -> updateButtons(ScrambleQueueFrame.this.table));

        // up button
        this.buttonUp.addActionListener(event -> {
            JTable table1 = ScrambleQueueFrame.this.table;
            int[] selectedRows = table1.getSelectedRows();

            // move scrambles
            scrambleManager.moveScramblesUp(selectedRows);

            // fix selection
            table1.removeRowSelectionInterval(0, selectedRows.length - 1);
            for (int selectedRow : selectedRows) {
                table1.addRowSelectionInterval(selectedRow - 1, selectedRow - 1);
            }

            // request focus
            ScrambleQueueFrame.this.buttonUp.requestFocusInWindow();
        });

        // down button
        this.buttonDown.addActionListener(event -> {
            JTable table1 = ScrambleQueueFrame.this.table;
            int[] selectedRows = table1.getSelectedRows();

            // move scrambles
            scrambleManager.moveScramblesDown(selectedRows);

            // fix selection
            table1.removeRowSelectionInterval(0, selectedRows.length - 1);
            for (int selectedRow : selectedRows) {
                table1.addRowSelectionInterval(selectedRow + 1, selectedRow + 1);
            }

            // request focus
            ScrambleQueueFrame.this.buttonDown.requestFocusInWindow();
        });

        // remove button
        this.buttonRemove.addActionListener(event -> {
            // remove scrambles
            scrambleManager.removeScrambles(
                ScrambleQueueFrame.this.table.getSelectedRows());

            // request focus
            ScrambleQueueFrame.this.buttonRemove.requestFocusInWindow();
        });

        // import from file
        this.buttonImportFromFile.addActionListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(_("scramble_queue.scramble_file_description"), "txt"));
            int action = fileChooser.showOpenDialog(ScrambleQueueFrame.this);
            if (action != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Category category = categoryManager.getCurrentCategory();
            Scrambler scrambler = scramblerProvider.get(category.getScramblerId());
            String puzzleId = scrambler.getScramblerInfo().getPuzzleId();
            ScrambleParser scrambleParser = scrambleParserProvider.get(puzzleId);

            Scramble[] scrambles;
            try {
                scrambles = loadScramblesFromFile(
                    fileChooser.getSelectedFile(),
                    puzzleId + "-IMPORTER",
                    scrambleParser);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    ScrambleQueueFrame.this,
                    String.format(
                        _("scramble_queue.file_opening_error"),
                        fileChooser.getSelectedFile().getAbsolutePath()),
                    _("scramble_queue.error"),
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            scrambleManager.addScrambles(scrambles, false);
        });

        // export to file
        this.buttonExport.addActionListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(_("scramble_queue.default_file_name")));
            fileChooser.setFileFilter(new FileNameExtensionFilter(_("scramble_queue.scramble_file_description"), "txt"));
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
                    String.format(
                        _("scramble_queue.file_opening_error"),
                        fileChooser.getSelectedFile().getAbsolutePath()),
                    _("scramble_queue.error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // import from scrambler
        this.buttonImportFromScrambler.addActionListener(event -> {
            Scrambler scrambler =
                (Scrambler) ScrambleQueueFrame.this.comboBoxScrambler.getSelectedItem();

            Scramble[] scrambles = new Scramble[
                (Integer) ScrambleQueueFrame.this.spinnerNumberOfScrambles.getValue()];
            for (int i = 0; i < scrambles.length; i++) {
                scrambles[i] = scrambler.getNextScramble();
            }

            scrambleManager.addScrambles(scrambles, false);
        });

        // ok button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(event -> ScrambleQueueFrame.this.setVisible(false));

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
                arg0 -> ScrambleQueueFrame.this.setVisible(false),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "[fill][pref!]",
                "[pref!][]12[pref!][pref!]16[pref!]"));

        // labelQueue
        add(new JLabel(_("scramble_queue.queue")), "span, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane, "grow");

        // buttonUp
        this.buttonUp = new JButton(_("scramble_queue.up"));
        this.buttonUp.setEnabled(false);
        add(this.buttonUp, "top, growx, split 5, flowy");

        // buttonDown
        this.buttonDown = new JButton(_("scramble_queue.down"));
        this.buttonDown.setEnabled(false);
        add(this.buttonDown, "top, growx");

        // buttonRemove
        this.buttonRemove = new JButton(_("scramble_queue.remove"));
        this.buttonRemove.setEnabled(false);
        add(this.buttonRemove, "top, growx");

        // buttonImportFromFile
        this.buttonImportFromFile = new JButton(_("scramble_queue.import_from_file"));
        add(this.buttonImportFromFile, "gaptop 20, top, growx");

        // buttonExport
        this.buttonExport = new JButton(_("scramble_queue.export_to_file"));
        this.buttonExport.setEnabled(false);
        add(this.buttonExport, "top, growx, wrap");

        // labelImportFromScrambler
        add(new JLabel(_("scramble_queue.import_from_scrambler")), "span, wrap");

        // comboBoxScrambler
        this.comboBoxScrambler = new JComboBox();
        add(this.comboBoxScrambler, "growx, span, split 3");

        // spinnerNumberOfScrambles
        this.spinnerNumberOfScrambles = new JSpinner(new SpinnerNumberModel(12, 1, 1000, 1));
        add(this.spinnerNumberOfScrambles, "");

        // buttonImportFromScrambler
        this.buttonImportFromScrambler = new JButton(_("scramble_queue.import"));
        add(this.buttonImportFromScrambler, "wrap");

        // buttonOK
        this.buttonOk = new JButton(_("scramble_queue.ok"));
        add(this.buttonOk, "tag ok, span");
    }

    private void updateTable(Scramble[] queue) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn(_("scramble_queue.#"));
        tableModel.addColumn(_("scramble_queue.scramble"));

        this.table.setModel(tableModel);

        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        TableColumn indexColumn = this.table.getColumnModel().getColumn(0);
        indexColumn.setPreferredWidth(50);

        TableColumn scrambleColumn = this.table.getColumnModel().getColumn(1);
        scrambleColumn.setPreferredWidth(1000);

        for (int i = 0; i < queue.length; i++) {
            tableModel.addRow(new Object[] {
                i + 1,
                queue[i].getRawSequence(),
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

    private Scramble[] loadScramblesFromFile(File file, String scramblerId, ScrambleParser scrambleParser) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        Scanner scanner = new Scanner(fileInputStream, "UTF-8");

        ArrayList<Scramble> scrambles = new ArrayList<Scramble>();
        while (scanner.hasNextLine()) {
            scrambles.add(
                new Scramble(
                    scramblerId,
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
            writer.append(scramble.getRawSequence());
            writer.append("\r\n");
        }

        writer.close();
    }
}

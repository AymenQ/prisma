package com.puzzletimer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.PuzzleInfo;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleBuilder;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerBuilder;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;

interface CategoryEditorListener {
    void categoryEdited(Category category);
}

@SuppressWarnings("serial")
class CategoryEditorDialog extends JDialog {
    private JTextField textFieldDescription;
    private JComboBox comboBoxPuzzle;
    private JComboBox comboBoxScrambler;
    private JButton buttonOk;
    private JButton buttonCancel;

    public CategoryEditorDialog(
            JFrame owner,
            boolean modal,
            final Category category,
            boolean isEditable,
            final CategoryEditorListener listener) {
        super(owner, modal);

        setTitle("Category Editor");
        setMinimumSize(new Dimension(480, 180));
        setPreferredSize(getMinimumSize());

        createComponents();

        // set category description
        this.textFieldDescription.setText(category.description);

        // fill puzzles combo box
        for (Puzzle puzzle : PuzzleBuilder.getPuzzles()) {
            this.comboBoxPuzzle.addItem(puzzle.getPuzzleInfo());
        }

        // fill scramblers combo box on puzzle selection
        this.comboBoxPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                CategoryEditorDialog.this.comboBoxScrambler.removeAllItems();

                PuzzleInfo selectedPuzzle = (PuzzleInfo) CategoryEditorDialog.this.comboBoxPuzzle.getSelectedItem();
                for (Scrambler scrambler : ScramblerBuilder.getScramblers()) {
                    ScramblerInfo scramblerInfo = scrambler.getScramblerInfo();
                    if (scramblerInfo.getPuzzleId().equals(selectedPuzzle.getPuzzleId())) {
                        CategoryEditorDialog.this.comboBoxScrambler.addItem(scramblerInfo);
                    }
                }
            }
        });

        // set puzzle/scrambler combo boxes editable
        this.comboBoxPuzzle.setEnabled(isEditable);
        this.comboBoxScrambler.setEnabled(isEditable);

        // set ok button behavior
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                category.scramblerId =
                    ((ScramblerInfo) CategoryEditorDialog.this.comboBoxScrambler.getSelectedItem()).getScramblerId();
                category.description =
                    CategoryEditorDialog.this.textFieldDescription.getText();

                listener.categoryEdited(category);

                dispose();
            }
        });

        // set cancel button behavior
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        // select puzzle
        ScramblerInfo categoryScramblerInfo = null;
        for (Scrambler scrambler : ScramblerBuilder.getScramblers()) {
            ScramblerInfo scramblerInfo = scrambler.getScramblerInfo();
            if (scramblerInfo.getScramblerId().equals(category.scramblerId)) {
                categoryScramblerInfo = scramblerInfo;
                break;
            }
        }

        for (int i = 0; i < this.comboBoxPuzzle.getItemCount(); i++) {
            PuzzleInfo puzzleInfo = (PuzzleInfo) this.comboBoxPuzzle.getItemAt(i);
            if (puzzleInfo.getPuzzleId().equals(categoryScramblerInfo.getPuzzleId())) {
                this.comboBoxPuzzle.setSelectedIndex(i);
                break;
            }
        }

        // select scrambler
        for (int i = 0; i < this.comboBoxScrambler.getItemCount(); i++) {
            ScramblerInfo scramblerInfo = (ScramblerInfo) this.comboBoxScrambler.getItemAt(i);
            if (scramblerInfo.getScramblerId().equals(categoryScramblerInfo.getScramblerId())) {
                this.comboBoxScrambler.setSelectedIndex(i);
                break;
            }
        }
    }

    private void createComponents() {
        setLayout(new MigLayout("fill, wrap", "[pref!][fill]", "[pref!]8[pref!]8[pref!]16[bottom]"));

        // labelDescription
        JLabel labelDescription = new JLabel("Description:");
        add(labelDescription);

        // textFieldDescription
        this.textFieldDescription = new JTextField();
        add(this.textFieldDescription);

        // labelPuzzle
        JLabel labelPuzzle = new JLabel("Puzzle:");
        add(labelPuzzle);

        // comboBoxPuzzle
        this.comboBoxPuzzle = new JComboBox();
        add(this.comboBoxPuzzle);

        // labelScrambler
        JLabel labelScrambler = new JLabel("Scrambler:");
        add(labelScrambler);

        // comboBoxScrambler
        this.comboBoxScrambler = new JComboBox();
        add(this.comboBoxScrambler);

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "right, width 100, span 2, split");

        // buttonCancel
        this.buttonCancel = new JButton("Cancel");
        add(this.buttonCancel, "width 100");
    }
}

@SuppressWarnings("serial")
public class CategoryManagerFrame extends JFrame {
    private JTable table;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonRemove;
    private JButton buttonOk;

    public CategoryManagerFrame(final CategoryManager categoryManager) {
        setTitle("Category Manager");
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(getMinimumSize());

        createComponents();

        categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                // set table data
                DefaultTableModel tableModel = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                for (String column : new String[] { "Description", "Puzzle", "Scrambler" }) {
                    tableModel.addColumn(column);
                }

                for (Category category : categories) {
                    ScramblerInfo scramblerInfo =
                        ScramblerBuilder.getScrambler(category.scramblerId).getScramblerInfo();
                    PuzzleInfo puzzleInfo =
                        PuzzleBuilder.getPuzzle(scramblerInfo.getPuzzleId()).getPuzzleInfo();

                    tableModel.addRow(new Object[] {
                        category.description,
                        puzzleInfo.getDescription(),
                        scramblerInfo.getDescription(),
                    });
                }

                CategoryManagerFrame.this.table.setModel(tableModel);
            }
        });

        // set table selection behavior
        CategoryManagerFrame.this.table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    int selectedIndex = CategoryManagerFrame.this.table.getSelectedRow();

                    if (selectedIndex < 0) {
                        CategoryManagerFrame.this.buttonEdit.setEnabled(false);
                        CategoryManagerFrame.this.buttonRemove.setEnabled(false);
                    } else {
                        Category category = categoryManager.getCategories()[selectedIndex];

                        boolean enabled =
                            category.isUserDefined() &&
                            category != categoryManager.getCurrentCategory();

                        CategoryManagerFrame.this.buttonEdit.setEnabled(enabled);
                        CategoryManagerFrame.this.buttonRemove.setEnabled(enabled);
                    }
                }
            });

        // set add button behavior
        this.buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Category category = new Category(
                    UUID.randomUUID(),
                    "RUBIKS-CUBE-RANDOM",
                    "New category",
                    true);

                CategoryEditorListener listener = new CategoryEditorListener() {
                    @Override
                    public void categoryEdited(Category category) {
                        categoryManager.addCategory(category);
                    }
                };

                CategoryEditorDialog dialog = new CategoryEditorDialog(
                    CategoryManagerFrame.this,
                    true,
                    category,
                    true,
                    listener);
                dialog.setVisible(true);
            }
        });

        // set edit button behavior
        this.buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int selectedIndex = CategoryManagerFrame.this.table.getSelectedRow();
                Category category = categoryManager.getCategories()[selectedIndex];

                CategoryEditorListener listener = new CategoryEditorListener() {
                    @Override
                    public void categoryEdited(Category category) {
                        categoryManager.updateCategory(category);
                    }
                };

                CategoryEditorDialog dialog = new CategoryEditorDialog(
                    CategoryManagerFrame.this,
                    true,
                    category,
                    true,
                    listener);
                dialog.setVisible(true);
            }
        });

        // set remove button behavior
        this.buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int selectedIndex = CategoryManagerFrame.this.table.getSelectedRow();
                Category category = categoryManager.getCategories()[selectedIndex];
                categoryManager.removeCategory(category);
            }
        });

        // set ok button behavior
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
            }
        });
    }

    private void createComponents() {
        setLayout(new MigLayout("fill", "[grow][pref!]", "[pref!]8[grow]16[pref!]"));

        // labelCategories
        JLabel labelCategories = new JLabel("Categories");
        add(labelCategories, "span 2, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        add(scrollPane, "grow");

        // buttonAdd
        this.buttonAdd = new JButton("Add...");
        add(this.buttonAdd, "top, growx, split, flowy");

        // buttonEdit
        this.buttonEdit = new JButton("Edit...");
        this.buttonEdit.setEnabled(false);
        add(this.buttonEdit, "growx");

        // buttonRemove
        this.buttonRemove = new JButton("Remove");
        this.buttonRemove.setEnabled(false);
        add(this.buttonRemove, "growx, wrap");

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "right, width 100, span 2");
    }
}

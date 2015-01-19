package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.PuzzleInfo;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.tips.Tip;
import com.puzzletimer.tips.TipProvider;

interface CategoryEditorListener {
    void categoryEdited(Category category);
}

@SuppressWarnings("serial")
class CategoryEditorDialog extends JDialog {
    private JTextField textFieldDescription;
    private JComboBox comboBoxPuzzle;
    private JComboBox comboBoxScrambler;
    private JComboBox comboBoxTips;
    private JButton buttonTipAdd;
    private JList listTips;
    private JButton buttonTipUp;
    private JButton buttonTipDown;
    private JButton buttonTipRemove;
    private JButton buttonSplitsAdd;
    private JList listSplits;
    private JButton buttonSplitsUp;
    private JButton buttonSplitsDown;
    private JButton buttonSplitsRemove;
    private JButton buttonOk;
    private JButton buttonCancel;

    public CategoryEditorDialog(
            JFrame owner,
            boolean modal,
            Puzzle[] puzzles,
            final Scrambler[] scramblers,
            final Tip[] tips,
            final Category category,
            boolean isEditable,
            final CategoryEditorListener listener) {
        super(owner, modal);

        setTitle(_("category_editor.category_editor"));
        setMinimumSize(new Dimension(480, 300));

        createComponents();
        pack();

        // set category description
        this.textFieldDescription.setText(category.getDescription());

        // fill puzzles combo box
        for (Puzzle puzzle : puzzles) {
            this.comboBoxPuzzle.addItem(puzzle.getPuzzleInfo());
        }

        // fill combo boxes on puzzle selection
        this.comboBoxPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                PuzzleInfo selectedPuzzle = (PuzzleInfo) CategoryEditorDialog.this.comboBoxPuzzle.getSelectedItem();

                // scramblers
                CategoryEditorDialog.this.comboBoxScrambler.removeAllItems();
                for (Scrambler scrambler : scramblers) {
                    ScramblerInfo scramblerInfo = scrambler.getScramblerInfo();
                    if (scramblerInfo.getPuzzleId().equals(selectedPuzzle.getPuzzleId())) {
                        CategoryEditorDialog.this.comboBoxScrambler.addItem(scramblerInfo);
                    }
                }

                // tips
                CategoryEditorDialog.this.comboBoxTips.removeAllItems();
                for (Tip tip : tips) {
                    if (tip.getPuzzleId().equals(selectedPuzzle.getPuzzleId())) {
                        CategoryEditorDialog.this.comboBoxTips.addItem(tip);
                    }
                }

                // selected tips
                DefaultListModel listModel = (DefaultListModel) CategoryEditorDialog.this.listTips.getModel();
                listModel.removeAllElements();
                for (String categoryTipId : category.getTipIds()) {
                    for (Tip tip : tips) {
                        if (categoryTipId.equals(tip.getTipId())) {
                            listModel.addElement(tip);
                            break;
                        }
                    }
                }
            }
        });

        // set add button behavior
        this.buttonTipAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Tip selectedTip = (Tip) CategoryEditorDialog.this.comboBoxTips.getSelectedItem();
                if (selectedTip == null) {
                    return;
                }

                DefaultListModel listModel = (DefaultListModel) CategoryEditorDialog.this.listTips.getModel();
                if (!listModel.contains(selectedTip)) {
                    listModel.addElement(selectedTip);
                }
            }
        });

        // set up button behavior
        this.buttonTipUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JList listTips = CategoryEditorDialog.this.listTips;

                DefaultListModel model = (DefaultListModel) listTips.getModel();

                int selectedIndex = listTips.getSelectedIndex();
                if (selectedIndex > 0) {
                    // swap
                    Object selectedValue = model.getElementAt(selectedIndex);
                    model.insertElementAt(selectedValue, selectedIndex - 1);
                    model.removeElementAt(selectedIndex + 1);

                    // fix selection
                    listTips.addSelectionInterval(selectedIndex - 1, selectedIndex - 1);
                }
            }
        });

        // set down button behavior
        this.buttonTipDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JList listTips = CategoryEditorDialog.this.listTips;

                DefaultListModel model = (DefaultListModel) listTips.getModel();

                int selectedIndex = listTips.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < model.getSize() - 1) {
                    // swap
                    Object selectedValue = model.getElementAt(selectedIndex);
                    model.insertElementAt(selectedValue, selectedIndex + 2);
                    model.removeElementAt(selectedIndex);

                    // fix selection
                    listTips.addSelectionInterval(selectedIndex + 1, selectedIndex + 1);
                }
            }
        });

        // set remove button behavior
        this.buttonTipRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                DefaultListModel model = (DefaultListModel) CategoryEditorDialog.this.listTips.getModel();

                int selectedIndex = CategoryEditorDialog.this.listTips.getSelectedIndex();
                if (selectedIndex >= 0) {
                    model.removeElementAt(selectedIndex);
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
                // scrambler
                String scramblerId =
                    ((ScramblerInfo) CategoryEditorDialog.this.comboBoxScrambler.getSelectedItem()).getScramblerId();

                // description
                String description =
                    CategoryEditorDialog.this.textFieldDescription.getText();

                // tip ids
                ListModel listModel = CategoryEditorDialog.this.listTips.getModel();

                String[] tipIds = new String[listModel.getSize()];
                for (int i = 0; i < tipIds.length; i++) {
                    tipIds[i] = ((Tip) listModel.getElementAt(i)).getTipId();
                }

                listener.categoryEdited(
                    category
                        .setScramblerId(scramblerId)
                        .setDescription(description)
                        .setTipIds(tipIds));

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
        for (Scrambler scrambler : scramblers) {
            ScramblerInfo scramblerInfo = scrambler.getScramblerInfo();
            if (scramblerInfo.getScramblerId().equals(category.getScramblerId())) {
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

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    CategoryEditorDialog.this.setVisible(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "[pref!][fill][pref!]",
                "[pref!]8[pref!]8[pref!]8[pref!][grow]16[pref!]"));

        add(new JLabel(_("category_editor.description")));

        // textFieldDescription
        this.textFieldDescription = new JTextField();
        add(this.textFieldDescription, "span 2, wrap");

        add(new JLabel(_("category_editor.puzzle")));

        // comboBoxPuzzle
        this.comboBoxPuzzle = new JComboBox();
        add(this.comboBoxPuzzle, "span 2, wrap");

        add(new JLabel(_("category_editor.scrambler")));

        // comboBoxScrambler
        this.comboBoxScrambler = new JComboBox();
        add(this.comboBoxScrambler, "span 2, wrap");

        add(new JLabel(_("category_editor.tips")));

        // comboBoxTips
        this.comboBoxTips = new JComboBox();
        add(this.comboBoxTips);

        // buttonAdd
        this.buttonTipAdd = new JButton(_("category_editor.add"));
        add(this.buttonTipAdd, "sizegroup button, wrap");

        // listTips
        this.listTips = new JList(new DefaultListModel());
        this.listTips.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(this.listTips);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane, "grow, skip");

        // buttonTipUp
        this.buttonTipUp = new JButton(_("category_editor.up"));
        add(this.buttonTipUp, "sizegroup button, top, split 3, flowy");

        // buttonTipDown
        this.buttonTipDown = new JButton(_("category_editor.down"));
        add(this.buttonTipDown, "sizegroup button");

        // buttonTipRemove
        this.buttonTipRemove = new JButton(_("category_editor.remove"));
        add(this.buttonTipRemove, "sizegroup button, wrap");

        // buttonOk
        this.buttonOk = new JButton(_("category_editor.ok"));
        add(this.buttonOk, "tag ok, span 3, split");

        // buttonCancel
        this.buttonCancel = new JButton(_("category_editor.cancel"));
        add(this.buttonCancel, "tag cancel");
    }
}

@SuppressWarnings("serial")
public class CategoryManagerFrame extends JFrame {
    private JTable table;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonRemove;
    private JButton buttonOk;

    public CategoryManagerFrame(
            final PuzzleProvider puzzleProvider,
            final ScramblerProvider scramblerProvider,
            final CategoryManager categoryManager,
            final TipProvider tipProvider) {
        setTitle(_("category_manager.category_manager"));
        setMinimumSize(new Dimension(640, 480));

        createComponents();
        pack();

        categoryManager.addListener(new CategoryManager.Listener() {
            @Override
            public void categoriesUpdated(Category[] categories, Category currentCategory) {
                // set table data
                DefaultTableModel tableModel = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                tableModel.addColumn(_("category_manager.description"));
                tableModel.addColumn(_("category_manager.puzzle"));
                tableModel.addColumn(_("category_manager.scrambler"));

                for (Category category : categories) {
                    ScramblerInfo scramblerInfo =
                        scramblerProvider.get(category.getScramblerId()).getScramblerInfo();
                    PuzzleInfo puzzleInfo =
                        puzzleProvider.get(scramblerInfo.getPuzzleId()).getPuzzleInfo();

                    tableModel.addRow(new Object[] {
                        category.getDescription(),
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
                        Category currentCategory = categoryManager.getCurrentCategory();

                        CategoryManagerFrame.this.buttonEdit.setEnabled(
                            category != currentCategory);
                        CategoryManagerFrame.this.buttonRemove.setEnabled(
                            category != currentCategory && category.isUserDefined());
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
                    _("category_manager.new_category"),
                    true,
                    new String[0]);

                CategoryEditorListener listener = new CategoryEditorListener() {
                    @Override
                    public void categoryEdited(Category category) {
                        categoryManager.addCategory(category);
                    }
                };

                CategoryEditorDialog dialog = new CategoryEditorDialog(
                    CategoryManagerFrame.this,
                    true,
                    puzzleProvider.getAll(),
                    scramblerProvider.getAll(),
                    tipProvider.getAll(),
                    category,
                    true,
                    listener);
                dialog.setLocationRelativeTo(null);
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
                    puzzleProvider.getAll(),
                    scramblerProvider.getAll(),
                    tipProvider.getAll(),
                    category,
                    false,
                    listener);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        // set remove button behavior
        this.buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int result = JOptionPane.showConfirmDialog(
                    CategoryManagerFrame.this,
                    _("category_manager.category_removal_confirmation_message"),
                    _("category_manager.remove_category"),
                    JOptionPane.YES_NO_CANCEL_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }

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

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    CategoryManagerFrame.this.setVisible(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(new MigLayout("fill", "[grow][pref!]", "[pref!]8[grow]16[pref!]"));

        // labelCategories
        JLabel labelCategories = new JLabel(_("category_manager.categories"));
        add(labelCategories, "span 2, wrap");

        // table
        this.table = new JTable();
        this.table.setShowVerticalLines(false);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(this.table);
        this.table.setFillsViewportHeight(true);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane, "grow");

        // buttonAdd
        this.buttonAdd = new JButton(_("category_manager.add"));
        add(this.buttonAdd, "top, growx, split, flowy");

        // buttonEdit
        this.buttonEdit = new JButton(_("category_manager.edit"));
        this.buttonEdit.setEnabled(false);
        add(this.buttonEdit, "growx");

        // buttonRemove
        this.buttonRemove = new JButton(_("category_manager.remove"));
        this.buttonRemove.setEnabled(false);
        add(this.buttonRemove, "growx, wrap");

        // buttonOk
        this.buttonOk = new JButton(_("category_manager.ok"));
        add(this.buttonOk, "tag ok, span 2");
    }
}

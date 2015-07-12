package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.state.ConfigurationManager;
import com.puzzletimer.util.SolutionUtils;

@SuppressWarnings("serial")
public class SolutionEditingDialog extends JDialog {
    public static class SolutionEditingDialogListener {
        public void solutionEdited(Solution solution) {
        }
    }

    private JTextField textFieldStart;
    private JTextField textFieldTime;
    private JComboBox comboBoxPenalty;
    private JTextField textFieldScramble;
    private JTextField textFieldComment;
    private JButton buttonOk;
    private JButton buttonCancel;

    public SolutionEditingDialog(
            JFrame owner,
            boolean modal,
            final Solution solution,
            final SolutionEditingDialogListener listener,
            final ConfigurationManager configurationManager) {
        super(owner, modal);

        setTitle(_("solution_editing.solution_editor"));
        setMinimumSize(new Dimension(480, 200));

        createComponents();
        pack();

        // start
        DateFormat dateFormat =
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        this.textFieldStart.setText(
            dateFormat.format(solution.getTiming().getStart()));

        // time
        this.textFieldTime.setText(
            SolutionUtils.formatMinutes(solution.getTiming().getElapsedTime(), configurationManager.getConfiguration("TIMER-PRECISION"), false));
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // text selection
                SolutionEditingDialog.this.textFieldTime.setSelectionStart(0);
                SolutionEditingDialog.this.textFieldTime.setSelectionEnd(Integer.MAX_VALUE);

                // focus
                SolutionEditingDialog.this.textFieldTime.requestFocusInWindow();

            }
        });

        // penalty
        this.comboBoxPenalty.addItem("");
        this.comboBoxPenalty.addItem("+2");
        this.comboBoxPenalty.addItem("DNF");
        this.comboBoxPenalty.setSelectedItem(solution.getPenalty());

        // scramble
        this.textFieldScramble.setText(solution.getScramble().getRawSequence());
        this.textFieldScramble.setCaretPosition(0);

        // comment
        this.textFieldComment.setText(solution.getComment());

        // ok button
        this.buttonOk.addActionListener(event -> {
            // timing
            long time =
                SolutionUtils.parseTime(
                    SolutionEditingDialog.this.textFieldTime.getText());
            Timing timing =
                new Timing(
                    solution.getTiming().getStart(),
                    new Date(solution.getTiming().getStart().getTime() + time));

            // penalty
            String penalty =
                (String) SolutionEditingDialog.this.comboBoxPenalty.getSelectedItem();

            String comment = SolutionEditingDialog.this.textFieldComment.getText();

            listener.solutionEdited(
                solution
                    .setTiming(timing)
                    .setPenalty(penalty)
                    .setComment(comment));

            SolutionEditingDialog.this.dispose();
        });
        this.getRootPane().setDefaultButton(this.buttonOk);

        // cancel button
        this.buttonCancel.addActionListener(event -> SolutionEditingDialog.this.dispose());

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
                arg0 -> SolutionEditingDialog.this.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill, wrap",
                "[pref!][fill]",
                "[pref!]8[pref!]8[pref!]8[pref!]16[bottom]"));

        // labelStart
        add(new JLabel(_("solution_editing.start")));

        // textFieldStart
        this.textFieldStart = new JTextField();
        this.textFieldStart.setEditable(false);
        this.textFieldStart.setFocusable(false);
        add(this.textFieldStart);

        // labelTime
        add(new JLabel(_("solution_editing.time")));

        // textFieldTime
        this.textFieldTime = new JTextField();
        add(this.textFieldTime);

        // labelPenalty
        add(new JLabel(_("solution_editing.penalty")));

        // comboBoxPenalty
        this.comboBoxPenalty = new JComboBox();
        add(this.comboBoxPenalty);

        // labelScramble
        add(new JLabel(_("solution_editing.scramble")));

        // textFieldScramble
        this.textFieldScramble = new JTextField();
        this.textFieldScramble.setEditable(false);
        this.textFieldScramble.setFocusable(false);
        add(this.textFieldScramble);

        // labelComment
        add(new JLabel(_("solution_editing.comment")));

        //textFieldComment

        this.textFieldComment = new JTextField();
        add(this.textFieldComment);

        // buttonOk
        this.buttonOk = new JButton(_("solution_editing.ok"));
        add(this.buttonOk, "tag ok, span 2, split");

        // buttonCancel
        this.buttonCancel = new JButton(_("solution_editing.cancel"));
        add(this.buttonCancel, "tag cancel");
    }
}

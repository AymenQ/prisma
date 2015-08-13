package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.puzzletimer.state.TimerManager;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class StackmatDeveloperFrame extends JFrame {
    private JTextArea textAreaSummary;
    private JButton buttonUpdate;
    private JButton buttonCopyToClipboard;
    private JButton buttonOk;
    private StackmatGraphPanel graphPanel;

    public StackmatDeveloperFrame(TimerManager timerManager) {
        super();

        setMinimumSize(new Dimension(800, 600));

        createComponents();
        pack();
        setTitle(_("stackmat_developer.title"));
        
        timerManager.addListener(new TimerManager.Listener() {
            @Override
            public void dataNotReceived(byte[] data) {
                updateSummary(data);
            }
        });

        // update
        this.buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //StackmatDeveloperFrame.this.updateSummary();
            }
        });

        // copy to clipboard
        this.buttonCopyToClipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                StringSelection contents =
                    new StringSelection(StackmatDeveloperFrame.this.textAreaSummary.getText());
                Clipboard clipboard =
                    Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(contents, contents);
            }
        });

        // ok button
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                StackmatDeveloperFrame.this.setVisible(false);
            }
        });

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    StackmatDeveloperFrame.this.setVisible(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill",
                "",
                "[pref!][pref!]16[pref!][][pref!]16[pref!]"));
        
        // labelGraph
        add(new JLabel(_("stackmat_developer.graph")), "span, wrap");

        // Graph
        this.graphPanel = new StackmatGraphPanel();
        add(this.graphPanel, "growx, height 90, span, wrap");

        // labelRawData
        add(new JLabel(_("stackmat_developer.raw_data")), "wrap");

        // textAreaContents
        this.textAreaSummary = new JTextArea();
        this.textAreaSummary.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(this.textAreaSummary);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane, "grow, wrap");

        // button update
        this.buttonUpdate = new JButton(_("stackmat_developer.update"));
        add(this.buttonUpdate, "split, grow");

        // button copy to clipboard
        this.buttonCopyToClipboard = new JButton(_("stackmat_developer.copy_to_clipboard"));
        add(this.buttonCopyToClipboard, "width 150, right, wrap");

        // buttonOk
        this.buttonOk = new JButton(_("stackmat_developer.ok"));
        add(this.buttonOk, "tag ok");
    }

    public void updateSummary(byte[] data) {
        String text = "";
        if(data != null){
	        for(int i = 0; i < data.length; i++) {
	            text = text + data[i] + " ";
	        }
	        this.graphPanel.setData(data);
	        this.textAreaSummary.setText(text);
        } else {
        	this.textAreaSummary.setText(_("stackmat_developer.error"));
        }
    }
}

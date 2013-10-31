package com.puzzletimer.gui;

import static com.puzzletimer.Internationalization._;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.puzzletimer.state.ConfigurationManager;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class StackmatDeveloperFrame extends JFrame {
    private ConfigurationManager configurationManager;
    private AudioFormat audioFormat;
    private Mixer.Info mixerInfo;
    private JTextArea textAreaSummary;
    private JButton buttonUpdate;
    private JButton buttonCopyToClipboard;
    private JButton buttonOk;
    public byte[] data;

    public StackmatDeveloperFrame(ConfigurationManager configurationManager) {
        super();

        setMinimumSize(new Dimension(640, 480));
        this.configurationManager = configurationManager;

        createComponents();
        pack();
        setTitle(_("stackmat_developer.title"));

        // update
        this.buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String timerTrigger = StackmatDeveloperFrame.this.configurationManager.getConfiguration("TIMER-TRIGGER");
                if (timerTrigger.equals("STACKMAT-TIMER")) {
                    StackmatDeveloperFrame.this.updateSummary();
                } else {
                    TargetDataLine targetDataLine = null;
                    StackmatDeveloperFrame.this.audioFormat = new AudioFormat(8000, 8, 1, true, false);
                    StackmatDeveloperFrame.this.mixerInfo = null;
                    String stackmatTimerInputDeviceName = StackmatDeveloperFrame.this.configurationManager.getConfiguration("STACKMAT-TIMER-INPUT-DEVICE");

                    for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                        if (stackmatTimerInputDeviceName.equals(mixerInfo.getName())) {
                            StackmatDeveloperFrame.this.mixerInfo = mixerInfo;
                            break;
                        }
                    }
                    if (StackmatDeveloperFrame.this.mixerInfo != null) {
                        try {
                            targetDataLine = AudioSystem.getTargetDataLine(StackmatDeveloperFrame.this.audioFormat, StackmatDeveloperFrame.this.mixerInfo);
                            targetDataLine.open(StackmatDeveloperFrame.this.audioFormat);
                        } catch (LineUnavailableException e1) {}
                    }
                    StackmatDeveloperFrame.this.updateSummary(targetDataLine);
                }
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
                "[pref!][][pref!]16[pref!]"));

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

    public void updateSummary() {
        String text = "";
        for(int i = 0; i < this.data.length; i++) {
            text = text + this.data[i] + " ";
        }
        this.textAreaSummary.setText(text);
    }

    public void updateSummary(TargetDataLine targetDataLine) {
        targetDataLine.start();
        double sampleRate = targetDataLine.getFormat().getFrameRate();
        this.data = new byte[(int) (sampleRate / 4)];
        targetDataLine.read(this.data, 0, this.data.length);
        targetDataLine.close();
        this.updateSummary();
    }
}

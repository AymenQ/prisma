package com.puzzletimer.timer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.JTextField;

import com.puzzletimer.models.Timing;
import com.puzzletimer.state.TimerManager;
import com.puzzletimer.util.SolutionUtils;

public class ManualInputTimer implements Timer {

    private TimerManager timerManager;
    private KeyListener keyListener;
    private Date start;
    private JTextField textFieldTime;

    public ManualInputTimer(TimerManager timerManager, JTextField textFieldTime) {
        this.textFieldTime = textFieldTime;
        this.timerManager = timerManager;
        this.start = null;
    }

    @Override
    public String getTimerId() {
        return "MANUAL-INPUT";
    }

    @Override
    public void start() {
        this.keyListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                ManualInputTimer.this.start = new Date();
                long time =
                    SolutionUtils.parseTime(
                        ManualInputTimer.this.textFieldTime.getText());
               Timing timing =
                   new Timing(
                       ManualInputTimer.this.start,
                       new Date(ManualInputTimer.this.start.getTime() + time));
                ManualInputTimer.this.timerManager.finishSolution(timing);
                ManualInputTimer.this.textFieldTime.setText(null);
            }
        };

        this.textFieldTime.addKeyListener(this.keyListener);
    }

    @Override
    public void setInspectionEnabled(boolean inspectionEnabled) {    }

    @Override
    public void stop() {
        this.textFieldTime.removeKeyListener(this.keyListener);
    }

	@Override
	public void setSmoothTimingEnabled(boolean smoothTimingEnabled) {
		// TODO Auto-generated method stub
		
	}
}

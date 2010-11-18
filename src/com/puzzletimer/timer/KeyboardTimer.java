package com.puzzletimer.timer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;

import javax.swing.JFrame;

import com.puzzletimer.models.Timing;

interface TimerStateListener extends EventListener {
    void timerStarted();
    void timerStopped();
}

class TimerState {
    private boolean readyToStart;
    private boolean running;
    private boolean leftPressed;
    private boolean rightPressed;
    private ArrayList<TimerStateListener> listeners;

    public TimerState() {
        this.readyToStart = true;
        this.running = false;
        this.leftPressed = false;
        this.rightPressed = false;
        this.listeners = new ArrayList<TimerStateListener>();
    }

    public void pressLeftButton() {
        pressButton(true);
    }

    public void releaseLeftButton() {
        releaseButton(true);
    }

    public void pressRightButton() {
        pressButton(false);
    }

    public void releaseRightButton() {
        releaseButton(false);
    }

    private void pressButton(boolean leftButton) {
        if (leftButton) {
            this.leftPressed = true;
        } else {
            this.rightPressed = true;
        }

        if (this.running && this.leftPressed && this.rightPressed) {
            this.running = false;
            for (TimerStateListener listener : this.listeners) {
                listener.timerStopped();
            }
        }
    }

    private void releaseButton(boolean leftButton) {
        if (leftButton) {
            this.leftPressed = false;
        } else {
            this.rightPressed = false;
        }

        if (this.readyToStart && !this.running && (this.leftPressed != this.rightPressed)) {
            this.readyToStart = false;
            this.running = true;
            for (TimerStateListener listener : this.listeners) {
                listener.timerStarted();
            }
        }

        if (!this.readyToStart && !this.running && !this.leftPressed && !this.rightPressed) {
            this.readyToStart = true;
        }
    }

    public void addEventListener(TimerStateListener listener) {
        this.listeners.add(listener);
    }

    public void removeEventListener(TimerStateListener listener) {
        this.listeners.remove(listener);
    }
}

public class KeyboardTimer implements Timer, TimerStateListener, KeyListener {
    private JFrame parent;
    private int keyCode;
    private Date start;
    private java.util.Timer repeater;
    private TimerState state;
    private ArrayList<TimerListener> listeners;

    public KeyboardTimer(JFrame parent, int keyCode)
    {
        this.parent = parent;
        this.keyCode = keyCode;
        this.start = null;
        this.state = new TimerState();
        this.listeners = new ArrayList<TimerListener>();
    }

    @Override
    public String getTimerId() {
        if (this.keyCode == KeyEvent.VK_CONTROL) {
            return "KEYBOARD-TIMER-CONTROL";
        } else if (this.keyCode == KeyEvent.VK_SPACE) {
            return "KEYBOARD-TIMER-SPACE";
        }

        return null;
    }

    @Override
    public void start()
    {
        this.state.addEventListener(this);
        this.parent.addKeyListener(this);
    }

    @Override
    public void stop()
    {
        if (this.repeater != null) {
            this.repeater.cancel();
        }
        this.state.removeEventListener(this);
        this.parent.removeKeyListener(this);
    }

    @Override
    public void timerStarted() {
        this.start = new Date();

        // timer ready
        for (TimerListener listener : this.listeners) {
            listener.timerReady();
        }

        // timer running
        this.repeater = new java.util.Timer();
        this.repeater.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Timing timing = new Timing(KeyboardTimer.this.start, new Date());
                for (TimerListener listener : KeyboardTimer.this.listeners) {
                    listener.timerRunning(timing);
                }
            }
        }, 0, 5);
    }

    @Override
    public void timerStopped() {
        this.repeater.cancel();

        Timing timing = new Timing(this.start, new Date());
        for (TimerListener listener : this.listeners) {
            listener.timerStopped(timing);
            listener.timerReady();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == this.keyCode) {
            switch (keyEvent.getKeyLocation()) {
                case KeyEvent.KEY_LOCATION_LEFT:
                    for (TimerListener listener : this.listeners) {
                        listener.leftHandPressed();
                    }
                    this.state.pressLeftButton();
                    break;

                case KeyEvent.KEY_LOCATION_RIGHT:
                    for (TimerListener listener : this.listeners) {
                        listener.rightHandPressed();
                    }
                    this.state.pressRightButton();
                    break;

                default:
                    for (TimerListener listener : this.listeners) {
                        listener.leftHandPressed();
                    }
                    this.state.pressLeftButton();
                    for (TimerListener listener : this.listeners) {
                        listener.rightHandPressed();
                    }
                    this.state.pressRightButton();
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == this.keyCode) {
            switch (keyEvent.getKeyLocation()) {
                case KeyEvent.KEY_LOCATION_LEFT:
                    for (TimerListener listener : this.listeners) {
                        listener.leftHandReleased();
                    }
                    this.state.releaseLeftButton();
                    break;

                case KeyEvent.KEY_LOCATION_RIGHT:
                    for (TimerListener listener : this.listeners) {
                        listener.rightHandReleased();
                    }
                    this.state.releaseRightButton();
                    break;

                default:
                    for (TimerListener listener : this.listeners) {
                        listener.leftHandReleased();
                    }
                    this.state.releaseLeftButton();
                    for (TimerListener listener : this.listeners) {
                        listener.rightHandReleased();
                    }
                    this.state.releaseRightButton();
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void addEventListener(TimerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeEventListener(TimerListener listener) {
        this.listeners.remove(listener);
    }
}

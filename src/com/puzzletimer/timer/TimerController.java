package com.puzzletimer.timer;

import javax.swing.event.EventListenerList;

public class TimerController {
    private boolean readyToStart, running, leftPressed, rightPressed;
    private EventListenerList listeners;

    public TimerController() {
        readyToStart = true;
        running = false;
        leftPressed = false;
        rightPressed = false;
        listeners = new EventListenerList();
    }

    public void reset() {
        readyToStart = true;
        running = false;
        leftPressed = false;
        rightPressed = false;
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
            leftPressed = true;
        } else {
            rightPressed = true;
        }

        if (running && leftPressed && rightPressed) {
            running = false;
            for (Object listener : listeners.getListeners(TimerControllerListener.class)) {
                ((TimerControllerListener) listener).timerStopped(new TimerControllerEvent(this));
            }
        }
    }

    private void releaseButton(boolean leftButton) {
        if (leftButton) {
            leftPressed = false;
        } else {
            rightPressed = false;
        }

        if (readyToStart && !running && ((leftPressed && !rightPressed) || (!leftPressed && rightPressed))) {
            readyToStart = false;
            running = true;
            for (Object listener : listeners.getListeners(TimerControllerListener.class)) {
                ((TimerControllerListener) listener).timerStarted(new TimerControllerEvent(this));
            }
        }

        if (!readyToStart && !running && !leftPressed && !rightPressed) {
            readyToStart = true;
        }
    }

    public void addEventListener(TimerControllerListener listener) {
        listeners.add(TimerControllerListener.class, listener);
    }

    public void removeEventListener(TimerControllerListener listener) {
        listeners.remove(TimerControllerListener.class, listener);
    }
}

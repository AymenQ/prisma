package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.models.Timing;
import com.puzzletimer.timer.Timer;

public class TimerManager {
    private ArrayList<TimerListener> listeners;
    private Timer currentTimer;

    public TimerManager() {
        this.listeners = new ArrayList<TimerListener>();
        this.currentTimer = null;
    }

    public void setTimer(Timer timer) {
        if (this.currentTimer != null) {
            this.currentTimer.stop();
        }

        this.currentTimer = timer;

        if (timer != null) {
            for (TimerListener listener : this.listeners) {
                listener.timerChanged(timer);
            }

            this.currentTimer.start();
        }
    }

    public void leftHandPressed() {
        for (TimerListener listener : this.listeners) {
            listener.leftHandPressed();
        }
    }

    public void leftHandReleased() {
        for (TimerListener listener : this.listeners) {
            listener.leftHandReleased();
        }
    }

    public void rightHandPressed() {
        for (TimerListener listener : this.listeners) {
            listener.rightHandPressed();
        }
    }

    public void rightHandReleased() {
        for (TimerListener listener : this.listeners) {
            listener.rightHandReleased();
        }
    }

    public void timerReady() {
        for (TimerListener listener : this.listeners) {
            listener.timerReady();
        }
    }

    public void timerRunning(Timing timing) {
        for (TimerListener listener : this.listeners) {
            listener.timerRunning(timing);
        }
    }

    public void timerStopped(Timing timing) {
        for (TimerListener listener : this.listeners) {
            listener.timerStopped(timing);
        }
    }

    public void addTimerListener(TimerListener listener) {
        this.listeners.add(listener);
    }

    public void removeTimerListener(TimerListener listener) {
        this.listeners.remove(listener);
    }
}

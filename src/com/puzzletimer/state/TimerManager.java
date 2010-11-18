package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.timer.Timer;
import com.puzzletimer.timer.TimerListener;

public class TimerManager {
    private ArrayList<TimerListener> listeners;
    private Timer currentTimer;

    public TimerManager(Timer timer) {
        this.listeners = new ArrayList<TimerListener>();
        this.currentTimer = timer;
        this.currentTimer.start();
    }

    public void setTimer(Timer timer) {
        if (this.currentTimer != null) {
            for (TimerListener listener : this.listeners) {
                this.currentTimer.removeEventListener(listener);
            }
            this.currentTimer.stop();
        }

        this.currentTimer = timer;

        if (timer != null) {
            for (TimerListener listener : this.listeners) {
                listener.timerChanged(timer);
                this.currentTimer.addEventListener(listener);
            }
            this.currentTimer.start();
        }
    }

    public void addTimerListener(TimerListener listener) {
        this.listeners.add(listener);
        this.currentTimer.addEventListener(listener);
    }

    public void removeTimerListener(TimerListener listener) {
        this.listeners.remove(listener);
        this.currentTimer.removeEventListener(listener);
    }
}

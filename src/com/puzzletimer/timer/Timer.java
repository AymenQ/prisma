package com.puzzletimer.timer;

public interface Timer {
    String getTimerId();
    void start();
    void stop();
    void addEventListener(TimerListener listener);
    void removeEventListener(TimerListener listener);
}

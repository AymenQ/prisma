package com.puzzletimer.timer;

public interface Timer {
    void start();
    void stop();
    void addEventListener(TimerListener listener);
    void removeEventListener(TimerListener listener);
}

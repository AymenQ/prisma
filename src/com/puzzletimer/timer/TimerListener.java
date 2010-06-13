package com.puzzletimer.timer;

public interface TimerListener {
    void leftHandPressed();
    void leftHandReleased();
    void rightHandPressed();
    void rightHandReleased();
    void timerReady();
    void timerRunning(Timing timing);
    void timerStopped(Timing timing);
}

package com.puzzletimer.timer;

import com.puzzletimer.models.Timing;

public interface TimerListener {
    void leftHandPressed();
    void leftHandReleased();
    void rightHandPressed();
    void rightHandReleased();
    void timerReady();
    void timerRunning(Timing timing);
    void timerStopped(Timing timing);
}

package com.puzzletimer.state;

import com.puzzletimer.models.Timing;
import com.puzzletimer.timer.Timer;

public class TimerListener {
    // timer

    public void timerChanged(Timer timer) {
    }


    // hands

    public void leftHandPressed() {
    }

    public void leftHandReleased() {
    }

    public void rightHandPressed() {
    }

    public void rightHandReleased() {
    }


    // inspection

    public void inspectionEnabledSet(boolean inspectionEnabled) {
    }

    public void inspectionStarted() {
    }

    public void inspectionRunning(long remainingTime) {
    }

    public void inspectionFinished() {
    }

    // solution

    public void solutionStarted() {
    }

    public void solutionRunning(Timing timing) {
    }

    public void solutionFinished(Timing timing, String penalty) {
    }
}

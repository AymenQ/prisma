package com.puzzletimer.timer;

import java.util.EventListener;

public interface TimerControllerListener extends EventListener {
    public void timerStarted(TimerControllerEvent event);
    public void timerStopped(TimerControllerEvent event);
}

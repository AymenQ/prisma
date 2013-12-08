package com.puzzletimer.timer;

public interface Timer {
    String getTimerId();
    void setInspectionEnabled(boolean inspectionEnabled);
    void setSmoothTimingEnabled(boolean smoothTimingEnabled);
    void start();
    void stop();
}

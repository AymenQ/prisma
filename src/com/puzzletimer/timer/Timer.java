package com.puzzletimer.timer;

public interface Timer {
    String getTimerId();
    void setInspectionEnabled(boolean inspectionEnabled);
    void start();
    void stop();
}

package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public interface StatisticalMeasure {
    String getDescription();
    int getMinimumWindowSize();
    int getMaximumWindowSize();
    int getWindowPosition();
    long getValue();
    void setSolutions(Solution[] solutions);
}

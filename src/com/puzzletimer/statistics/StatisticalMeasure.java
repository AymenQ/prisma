package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public interface StatisticalMeasure {
    int getMinimumWindowSize();
    int getMaximumWindowSize();
    int getWindowPosition();
    long getValue();
    void setSolutions(Solution[] solutions);
}

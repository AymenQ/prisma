package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public interface StatisticalMeasure {
    String getDescription();
    int getMinimumWindowSize();
    int getMaximumWindowSize();
    long calculate(Solution[] solutions);
}

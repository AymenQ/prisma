package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public interface StatisticalMeasure {
    long getValue(ArrayList<Solution> solutions);
}

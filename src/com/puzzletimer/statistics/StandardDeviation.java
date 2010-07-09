package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class StandardDeviation implements StatisticalMeasure {
    @Override
    public long getValue(ArrayList<Solution> solutions) {
        long average = new Average().getValue(solutions);

        long sum = 0;
        for (Solution solution : solutions) {
            long deviation = solution.getTiming().getElapsedTime() - average;
            sum += deviation * deviation;
        }

        return (long) Math.sqrt(sum / solutions.size());
    }
}

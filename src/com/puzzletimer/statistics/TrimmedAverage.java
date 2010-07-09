package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class TrimmedAverage implements StatisticalMeasure {
    @Override
    public long getValue(ArrayList<Solution> solutions) {
        if (solutions.size() < 3) {
            return 0;
        }

        long average = new Average().getValue(solutions);
        long best = new Best().getValue(solutions);
        long worst = new Worst().getValue(solutions);

        return (average * solutions.size() - best - worst) / (solutions.size() - 2);
    }
}

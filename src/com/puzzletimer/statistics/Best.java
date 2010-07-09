package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class Best implements StatisticalMeasure {
    @Override
    public long getValue(ArrayList<Solution> solutions) {
        long best = Long.MAX_VALUE;
        for (Solution solution : solutions) {
            if (solution.getTiming().getElapsedTime() < best) {
                best = solution.getTiming().getElapsedTime();
            }
        }

        return best;
    }
}

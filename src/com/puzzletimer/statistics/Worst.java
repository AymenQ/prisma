package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class Worst implements StatisticalMeasure {
    @Override
    public long getValue(ArrayList<Solution> solutions) {
        long worst = 0;
        for (Solution solution : solutions) {
            if (solution.getTiming().getElapsedTime() > worst) {
                worst = solution.getTiming().getElapsedTime();
            }
        }

        return worst;
    }
}

package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.Solution;

public class Worst implements StatisticalMeasure {
    @Override
    public long getValue(ArrayList<Solution> solutions) {
        long worst = 0;
        for (Solution solution : solutions) {
            if (solution.getTimer().getDiff() > worst) {
                worst = solution.getTimer().getDiff();
            }
        }

        return worst;
    }
}

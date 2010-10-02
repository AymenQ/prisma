package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class Worst implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public Worst(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Worst Time";
        if (this.maximumWindowSize < Integer.MAX_VALUE) {
            description += " (last " + this.maximumWindowSize + ")";
        }
        return description;
    }

    @Override
    public int getMinimumWindowSize() {
        return this.minimumWindowSize;
    }

    @Override
    public int getMaximumWindowSize() {
        return this.maximumWindowSize;
    }

    @Override
    public long calculate(Solution[] solutions) {
        long worst = 0L;
        for (Solution solution : solutions) {
            long time = solution.timing.getElapsedTime();
            if (solution.penalty.equals("+2")) {
                time += 2000L;
            } else if (solution.penalty.equals("DNF")) {
                time = Long.MAX_VALUE;
            }

            if (time > worst) {
                worst = time;
            }
        }

        return worst;
    }
}

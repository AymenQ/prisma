package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class Mean implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public Mean(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Mean";
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
        long sum = 0L;
        for (Solution solution : solutions) {
            sum += solution.getTiming().getElapsedTime();
        }

        return sum / solutions.length;
    }
}

package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class StandardDeviation implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public StandardDeviation(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Standard Deviation";
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
        double mean = 0d;
        for (Solution solution : solutions) {
            mean += solution.getTiming().getElapsedTime();
        }
        mean /= solutions.length;

        double variance = 0d;
        for (Solution solution : solutions) {
            variance += Math.pow(solution.getTiming().getElapsedTime() - mean, 2d);
        }
        variance /= solutions.length;

        return (long) Math.sqrt(variance);
    }
}

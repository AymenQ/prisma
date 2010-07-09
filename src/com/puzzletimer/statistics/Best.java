package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class Best implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public Best(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Best Time";
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
        long best = Long.MAX_VALUE;
        for (Solution solution : solutions) {
            if (solution.getTiming().getElapsedTime() < best) {
                best = solution.getTiming().getElapsedTime();
            }
        }

        return best;
    }
}

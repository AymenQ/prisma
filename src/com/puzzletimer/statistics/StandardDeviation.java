package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

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
        long[] times = SolutionUtils.realTimes(solutions, true);

        if (times.length == 0) {
            return 0L;
        }

        double mean = 0d;
        for (long time : times) {
            mean += time;
        }
        mean /= times.length;

        double variance = 0d;
        for (long time : times) {
            variance += Math.pow(time - mean, 2d);
        }
        variance /= times.length;

        return (long) Math.sqrt(variance);
    }
}

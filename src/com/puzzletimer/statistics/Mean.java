package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

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
        long[] times = SolutionUtils.getRealTimes(solutions, false);

        long sum = 0L;
        for (long time : times) {
            if (time == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }

            sum += time;
        }

        return sum / solutions.length;
    }
}

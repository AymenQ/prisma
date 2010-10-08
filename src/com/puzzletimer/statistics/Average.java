package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class Average implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public Average(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Average";
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

        // if number of DNFs is greater than one, return DNF
        int nDNFs = 0;
        for (long time : times) {
            if (time == Long.MAX_VALUE) {
                nDNFs++;
                if (nDNFs > 1) {
                    return Long.MAX_VALUE;
                }
            }
        }

        long worst = Long.MIN_VALUE;
        long best = Long.MAX_VALUE;

        long sum = 0L;
        for (long time : times) {
            if (time > worst) {
                worst = time;
            }

            if (time < best) {
                best = time;
            }

            sum += time;
        }

        return (sum - worst - best) / (solutions.length - 2);
    }
}

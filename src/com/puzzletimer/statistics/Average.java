package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

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
        // if number of DNFs is greater than one, return DNF
        int nDNFs = 0;
        for (Solution solution : solutions) {
            if (solution.penalty.equals("DNF")) {
                nDNFs++;
                if (nDNFs > 1) {
                    return Long.MAX_VALUE;
                }
            }
        }


        long worst = Long.MIN_VALUE;
        long best = Long.MAX_VALUE;

        long sum = 0L;
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

            if (time < best) {
                best = time;
            }

            sum += time;
        }

        return (sum - worst - best) / (solutions.length - 2);
    }
}

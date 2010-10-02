package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class BestAverage implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public BestAverage(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Best Mean";
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
        long bestAverage = Long.MAX_VALUE;
        for (int i = 0; i < solutions.length - this.minimumWindowSize + 1; i++) {
            long worst = Long.MIN_VALUE;
            long best = Long.MAX_VALUE;

            long sum = 0L;
            for (int j = 0; j < this.minimumWindowSize; j++) {
                long time = solutions[i + j].getTiming().getElapsedTime();

                if (time > worst) {
                    worst = time;
                }

                if (time < best) {
                    best = time;
                }

                sum += time;
            }

            long average = (sum - worst - best) / (this.minimumWindowSize - 2);
            if (average < bestAverage) {
                bestAverage = average;
            }
        }

        return bestAverage;
    }
}

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
        Average average = new Average(this.minimumWindowSize, this.minimumWindowSize);

        long bestAverage = Long.MAX_VALUE;
        for (int i = 0; i < solutions.length - this.minimumWindowSize + 1; i++) {
            Solution[] window = new Solution[this.minimumWindowSize];
            for (int j = 0; j < this.minimumWindowSize; j++) {
                window[j] = solutions[i + j];
            }

            long windowAverage = average.calculate(window);
            if (windowAverage < bestAverage) {
                bestAverage = windowAverage;
            }
        }

        return bestAverage;
    }
}

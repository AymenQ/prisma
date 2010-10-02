package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class BestMean implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public BestMean(int minimumWindowSize, int maximumWindowSize) {
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
        long bestMean = Long.MAX_VALUE;
        for (int i = 0; i < solutions.length - this.minimumWindowSize + 1; i++) {
            long sum = 0L;
            for (int j = 0; j < this.minimumWindowSize; j++) {
                sum += solutions[i + j].getTiming().getElapsedTime();
            }

            long mean = sum / this.minimumWindowSize;
            if (mean < bestMean) {
                bestMean = mean;
            }
        }

        return bestMean;
    }
}

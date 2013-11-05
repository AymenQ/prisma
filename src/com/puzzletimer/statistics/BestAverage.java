package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class BestAverage implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private int windowPosition;
    private long value;

    public BestAverage(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
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
    public int getWindowPosition() {
        return this.windowPosition;
    }

    @Override
    public long getValue() {
        return this.value;
    }
    
    @Override
    public boolean getRound() {
    	return true;
    }

    @Override
    public void setSolutions(Solution[] solutions, boolean round) {
        long[] times = SolutionUtils.realTimes(solutions, false, round);

        long bestAverage = Long.MAX_VALUE;
        for (int i = 0; i < times.length - this.minimumWindowSize + 1; i++) {
            long worst = Long.MIN_VALUE;
            long best = Long.MAX_VALUE;
            long sum = 0;
            for (int j = 0; j < this.minimumWindowSize; j++) {
                if (times[i + j] == Long.MAX_VALUE && worst == Long.MAX_VALUE) {
                    sum = Long.MAX_VALUE;
                    break;
                }

                if (times[i + j] > worst) {
                    worst = times[i + j];
                }

                if (times[i + j] < best) {
                    best = times[i + j];
                }

                sum += times[i + j];
            }

            if (sum == Long.MAX_VALUE) {
                continue;
            }

            long average = (sum - worst - best) / (this.minimumWindowSize - 2);

            if (average < bestAverage) {
                bestAverage = average;
                this.windowPosition = i;
            }
        }

        this.value = bestAverage;
    }
}

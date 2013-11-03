package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class StandardDeviation implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private long value;

    public StandardDeviation(int minimumWindowSize, int maximumWindowSize) {
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
        return 0;
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
        long[] times = SolutionUtils.realTimes(solutions, true, round);

        if (times.length == 0) {
            this.value = 0L;
            return;
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

        this.value = (long) Math.sqrt(variance);
    }
}

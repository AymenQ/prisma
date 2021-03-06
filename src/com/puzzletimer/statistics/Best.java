package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class Best implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private int windowPosition;
    private long value;

    public Best(int minimumWindowSize, int maximumWindowSize) {
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
    	return false;
    }

    @Override
    public void setSolutions(Solution[] solutions, boolean round) {
        long[] times = SolutionUtils.realTimes(solutions, false, round);

        long best = Long.MAX_VALUE;
        for (int i = 0; i < times.length; i++) {
            if (times[i] <= best) {
                best = times[i];
                this.windowPosition = i;
            }
        }

        this.value = best;
    }
}

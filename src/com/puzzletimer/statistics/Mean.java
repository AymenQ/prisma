package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class Mean implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private long value;

    public Mean(int minimumWindowSize, int maximumWindowSize) {
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
        long[] times = SolutionUtils.realTimes(solutions, false, round);

        long sum = 0L;
        for (long time : times) {
            if (time == Long.MAX_VALUE) {
                this.value = Long.MAX_VALUE;
                return;
            }

            sum += time;
        }

        this.value = sum / solutions.length;
    }
}

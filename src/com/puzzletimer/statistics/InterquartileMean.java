package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class InterquartileMean implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private long value;

    public InterquartileMean(int minimumWindowSize, int maximumWindowSize) {
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

        Percentile lowerQuartile =
            new Percentile(1, Integer.MAX_VALUE, 0.25);
        lowerQuartile.setSolutions(solutions, round);

        Percentile upperQuartile =
            new Percentile(1, Integer.MAX_VALUE, 0.75);
        upperQuartile.setSolutions(solutions, round);

        long sum = 0L;
        int nTimes = 0;
        for (long time : times) {
            if (time < lowerQuartile.getValue() || time > upperQuartile.getValue()) {
                continue;
            }

            if (time == Long.MAX_VALUE) {
                this.value = Long.MAX_VALUE;
                return;
            }

            sum += time;
            nTimes++;
        }

        this.value = sum / nTimes;
    }
}

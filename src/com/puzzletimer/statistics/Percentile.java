package com.puzzletimer.statistics;

import java.util.Arrays;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class Percentile implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private long value;
    private double p;

    public Percentile(int minimumWindowSize, int maximumWindowSize, double p) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
        this.p = p;
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
        Arrays.sort(times);

        double position = this.p * (solutions.length + 1);
        if (position < 1d) {
            this.value = times[0];
            return;
        } else if (position >= times.length) {
            this.value = times[times.length - 1];
            return;
        }

        int index = (int) Math.floor(position);

        if (times[index - 1] == Long.MAX_VALUE || (position - index != 0d && times[index] == Long.MAX_VALUE)) {
            this.value = Long.MAX_VALUE;
            return;
        }

        this.value = (long) (times[index - 1] + (position - index) * (times[index] - times[index - 1]));
    }
}

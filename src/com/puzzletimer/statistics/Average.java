package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;
import com.puzzletimer.util.SolutionUtils;

public class Average implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;
    private long value;

    public Average(int minimumWindowSize, int maximumWindowSize) {
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

        // if number of DNFs is greater than one, return DNF
        int nDNFs = 0;
        for (long time : times) {
            if (time == Long.MAX_VALUE) {
                nDNFs++;
                if (nDNFs > 1) {
                    this.value = Long.MAX_VALUE;
                    return;
                }
            }
        }

        long worst = Long.MIN_VALUE;
        long best = Long.MAX_VALUE;

        long sum = 0L;
        for (long time : times) {
            if (time > worst) {
                worst = time;
            }

            if (time < best) {
                best = time;
            }

            sum += time;
        }

        this.value = (sum - worst - best) / (solutions.length - 2);
    }
}

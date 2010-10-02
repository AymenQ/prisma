package com.puzzletimer.statistics;

import com.puzzletimer.models.Solution;

public class StandardDeviation implements StatisticalMeasure {
    private int minimumWindowSize;
    private int maximumWindowSize;

    public StandardDeviation(int minimumWindowSize, int maximumWindowSize) {
        this.minimumWindowSize = minimumWindowSize;
        this.maximumWindowSize = maximumWindowSize;
    }

    @Override
    public String getDescription() {
        String description = "Standard Deviation";
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
        int nNonDNFSolutions = 0;
        for (Solution solution : solutions) {
            if (!solution.penalty.equals("DNF")) {
                nNonDNFSolutions++;
            }
        }

        if (nNonDNFSolutions == 0) {
            return 0L;
        }

        double mean = 0d;
        for (Solution solution : solutions) {
            if (solution.penalty.equals("DNF")) {
                continue;
            }

            long time = solution.timing.getElapsedTime();
            if (solution.penalty.equals("+2")) {
                time += 2000L;
            }

            mean += time;
        }


        mean /= nNonDNFSolutions;

        double variance = 0d;
        for (Solution solution : solutions) {
            if (solution.penalty.equals("DNF")) {
                continue;
            }

            long time = solution.timing.getElapsedTime();
            if (solution.penalty.equals("+2")) {
                time += 2000L;
            }

            variance += Math.pow(time - mean, 2d);
        }
        variance /= nNonDNFSolutions;

        return (long) Math.sqrt(variance);
    }
}

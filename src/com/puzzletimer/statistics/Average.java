package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.Solution;

public class Average implements StatisticalMeasure {
	@Override
	public long getValue(ArrayList<Solution> solutions) {
        long sum = 0;
        for (Solution solution : solutions) {
            sum += solution.getTimer().getDiff();
        }

        return sum / solutions.size();
	}
}

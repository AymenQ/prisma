package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.Solution;

public class Best implements StatisticalMeasure {
	@Override
	public long getValue(ArrayList<Solution> solutions) {
		long best = Long.MAX_VALUE;
		for (Solution solution : solutions) {
			if (solution.getTimer().getDiff() < best) {
				best = solution.getTimer().getDiff();
			}
		}

		return best;
	}
}

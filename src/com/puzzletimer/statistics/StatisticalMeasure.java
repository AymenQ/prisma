package com.puzzletimer.statistics;

import java.util.ArrayList;

import com.puzzletimer.Solution;

public interface StatisticalMeasure {
	long getValue(ArrayList<Solution> solutions);
}

package com.puzzletimer.util;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class SolutionUtils {
    public static String formatSeconds(long time) {
        if (time == Long.MAX_VALUE) {
            return "DNF";
        }

        String sign = "";
        if (time < 0) {
            sign = "-";
            time = -time;
        }

        return sign + String.format(
            "%d.%02d",
            time / 1000,
            (time % 1000) / 10);
    }

    public static String formatMinutes(long time) {
        if (time == Long.MAX_VALUE) {
            return "DNF";
        }

        String sign = "";
        if (time < 0) {
            sign = "-";
            time = -time;
        }
        return sign + String.format(
            "%02d:%02d.%02d",
            time / 60000,
            (time % 60000) / 1000,
            (time % 1000) / 10);
    }

    public static long realTime(Solution solution) {
        if (solution.getPenalty().equals("DNF")) {
            return Long.MAX_VALUE;
        }

        if (solution.getPenalty().equals("+2")) {
            return solution.getTiming().getElapsedTime() + 2000;
        }

        return solution.getTiming().getElapsedTime();
    }

    public static long[] realTimes(Solution[] solutions, boolean filterDNF) {
        ArrayList<Long> realTimes = new ArrayList<Long>();
        for (int i = 0; i < solutions.length; i++) {
            long actualTime = realTime(solutions[i]);
            if (!filterDNF || actualTime != Long.MAX_VALUE) {
                realTimes.add(actualTime);
            }
        }

        long[] realTimesArray = new long[realTimes.size()];
        for (int i = 0; i < realTimesArray.length; i++) {
            realTimesArray[i] = realTimes.get(i);
        }

        return realTimesArray;
    }
}

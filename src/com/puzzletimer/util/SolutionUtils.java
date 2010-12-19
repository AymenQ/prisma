package com.puzzletimer.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Math.round((time % 1000) / 10.0));
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
            Math.round((time % 1000) / 10.0));
    }

    public static long parseTime(String input) {
        Pattern pattern;
        Matcher matcher;

        // 00:00.00
        pattern = Pattern.compile("\\s*(\\d+):(\\d+)\\.(\\d{1,2})\\s*");
        matcher = pattern.matcher(input);
        if (matcher.matches()) {
            long time = 0;

            // minutes
            time += 60000 * Integer.parseInt(matcher.group(1));

            // seconds
            time += 1000 * Integer.parseInt(matcher.group(2));

            // centiseconds
            int fraction = Integer.parseInt(matcher.group(3));
            time += fraction < 10 ? 100 * fraction : 10 * fraction;

            return time;
        }

        // 00:00
        pattern = Pattern.compile("\\s*(\\d+):(\\d+)\\s*");
        matcher = pattern.matcher(input);
        if (matcher.matches()) {
            long time = 0;

            // minutes
            time += 60000 * Integer.parseInt(matcher.group(1));

            // seconds
            time += 1000 * Integer.parseInt(matcher.group(2));

            return time;
        }

        // 00.00
        pattern = Pattern.compile("\\s*(\\d+)\\.(\\d{1,2})\\s*");
        matcher = pattern.matcher(input);
        if (matcher.matches()) {
            long time = 0;

            // seconds
            time += 1000 * Integer.parseInt(matcher.group(1));

            // centiseconds
            int fraction = Integer.parseInt(matcher.group(2));
            time += fraction < 10 ? 100 * fraction : 10 * fraction;

            return time;
        }

        // 00
        pattern = Pattern.compile("\\s*(\\d+)\\s*");
        matcher = pattern.matcher(input);
        if (matcher.matches()) {
            // seconds
            return 1000 * Integer.parseInt(matcher.group(1));
        }

        return 0;
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

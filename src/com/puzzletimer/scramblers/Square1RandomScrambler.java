package com.puzzletimer.scramblers;

import java.util.ArrayList;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public class Square1RandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private int scrambleLength;

    public Square1RandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        this.scramblerInfo = scramblerInfo;
        this.scrambleLength = scrambleLength;
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    private boolean[] rotateClockwise(boolean[] xs, int n) {
        boolean[] ys = new boolean[xs.length];
        for (int i = 0; i < xs.length; i++) {
            ys[i] = xs[(i + n) % ys.length];
        }
        return ys;
    }

    @Override
    public Scramble getNextScramble() {
        boolean t = true, f = false;
        boolean[] top = new boolean[] { t, t, f, t, t, f, t, t, f, t, t, f };
        boolean[] bottom = new boolean[] { t, f, t, t, f, t, t, f, t, t, f, t };

        Random r = new Random();

        ArrayList<String> sequence = new ArrayList<String>();
        int scrambleLength = 0; // turn metric
        while (scrambleLength < this.scrambleLength) {
            // top
            ArrayList<Integer> topOptions = new ArrayList<Integer>();
            for (int j = 0; j < 12; j++) {
                if (top[j] && top[(j + 6) % 12]) {
                    topOptions.add(j);
                }
            }

            int x = topOptions.get(r.nextInt(topOptions.size()));

            // bottom
            ArrayList<Integer> bottomOptions = new ArrayList<Integer>();
            for (int j = 0; j < 12; j++) {
                if (bottom[j] && bottom[(j + 6) % 12]) {
                    bottomOptions.add(j);
                }
            }

            int y = bottomOptions.get(r.nextInt(bottomOptions.size()));

            // discard null moves
            if (x == 0 && y == 0) {
                continue;
            }

            if (x > 0) {
                top = rotateClockwise(top, x);
                scrambleLength++;
            }

            if (y > 0) {
                bottom = rotateClockwise(bottom, y);
                scrambleLength++;
            }

            sequence.add("(" + (x <= 6 ? x : x - 12) + "," + (y <= 6 ? y : y - 12) + ")");

            // twist
            boolean[] newTop = new boolean[12];
            boolean[] newBottom = new boolean[12];

            for (int j = 0; j < 12; j++) {
                if (j < 7) {
                    newTop[j] = top[j];
                    newBottom[j] = bottom[j];
                } else {
                    newTop[j] = bottom[j];
                    newBottom[j] = top[j];
                }
            }

            top = newTop;
            bottom = newBottom;

            scrambleLength++;

            sequence.add("/");
        }

        String[] sequenceArray = new String[sequence.size()];
        sequence.toArray(sequenceArray);

        return new Scramble(
            getScramblerInfo().getScramblerId(),
            sequenceArray);
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

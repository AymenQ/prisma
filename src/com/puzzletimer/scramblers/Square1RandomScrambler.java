package com.puzzletimer.scramblers;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

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

        String[] sequence = new String[this.scrambleLength];
        for (int i = 0; i < this.scrambleLength; i++) {
            ArrayList<Integer> choices;
            int k;

            // top
            choices = new ArrayList<Integer>();
            k = 0;
            for (int j = 0; j < 12; j++) {
                if (top[j] && top[(j + 6) % 12]) {
                    choices.add(j);
                    k++;
                }
            }

            int x = choices.get(r.nextInt(choices.size()));
            top = rotateClockwise(top, x);

            // bottom
            choices = new ArrayList<Integer>();
            k = 0;
            for (int j = 0; j < 12; j++) {
                if (bottom[j] && bottom[(j + 6) % 12]) {
                    choices.add(j);
                    k++;
                }
            }

            int y = choices.get(r.nextInt(choices.size()));
            bottom = rotateClockwise(bottom, y);

            // right
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

            sequence[i] = "(" + (x <= 6 ? x : x - 12) + "," + (y <= 6 ? y : y - 12) + ")";
        }

        return new Scramble(
            UUID.randomUUID(),
            getScramblerInfo().getScramblerId(),
            sequence);
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

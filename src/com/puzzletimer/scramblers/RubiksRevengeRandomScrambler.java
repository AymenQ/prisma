package com.puzzletimer.scramblers;

import java.util.Arrays;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public class RubiksRevengeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private int scrambleLength;
    private Random random;

    public RubiksRevengeRandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        this.scramblerInfo = scramblerInfo;
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        int[] slice = new int[this.scrambleLength];
        int[] power = new int[this.scrambleLength];

        int i = 0;
        while (i < this.scrambleLength) {
            int s = this.random.nextInt(12);
            int p = this.random.nextInt(3);

            boolean ignore = false;
            for (int j = i - 1; j >= 0; j--) {
                // if not in the same axis
                if (s / 4 != slice[j] / 4) {
                    break;
                }

                if (s == slice[j]) {
                    ignore = true;
                }
            }

            if (!ignore) {
                slice[i] = s;
                power[i] = p;
                i++;
            }
        }

        // sort moves in the same axis
        i = 0;
        while (i < slice.length) {
            int len = 1;
            while (i + len < slice.length && slice[i] / 4 == slice[i + len] / 4) {
                len++;
            }

            Arrays.sort(slice, i, i + len);

            i += len;
        }

        String[][] moves = {
            { "U", "U2", "U'" }, { "Uw", "Uw2", "Uw'" },
            { "Dw", "Dw2", "Dw'" }, { "D", "D2", "D'" },
            { "L", "L2", "L'" }, { "Lw", "Lw2", "Lw'" },
            { "Rw", "Rw2", "Rw'" }, { "R", "R2", "R'" },
            { "F", "F2", "F'" }, { "Fw", "Fw2", "Fw'" },
            { "Bw", "Bw2", "Bw'" }, { "B", "B2", "B'" },
        };

        String[] sequence = new String[this.scrambleLength];
        for (int j = 0; j < sequence.length; j++) {
            sequence[j] = moves[slice[j]][power[j]];
        }

        return new Scramble(
            getScramblerInfo().getScramblerId(),
            sequence);
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

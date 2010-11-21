package com.puzzletimer.scramblers;

import java.util.Arrays;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public class VCube6RandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private int scrambleLength;
    private Random random;

    public VCube6RandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
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
            int s = this.random.nextInt(18);
            int p = this.random.nextInt(3);

            boolean ignore = false;
            for (int j = i - 1; j >= 0; j--) {
                // if not in the same axis
                if (s / 6 != slice[j] / 6) {
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
            while (i + len < slice.length && slice[i] / 6 == slice[i + len] / 6) {
                len++;
            }

            Arrays.sort(slice, i, i + len);

            i += len;
        }

        String[][] moves = {
            { "U", "U2", "U'" }, { "2U", "2U2", "2U'" }, { "3U", "3U2", "3U'" },
            { "3D", "3D2", "3D'" }, { "2D", "2D2", "2D'" }, { "D", "D2", "D'" },
            { "L", "L2", "L'" }, { "2L", "2L2", "2L'" }, { "3L", "3L2", "3L'" },
            { "3R", "3R2", "3R'" }, { "2R", "2R2", "2R'" }, { "R", "R2", "R'" },
            { "F", "F2", "F'" }, { "2F", "2F2", "2F'" }, { "3F", "3F2", "3F'" },
            { "3B", "3B2", "3B'" }, { "2B", "2B2", "2B'" }, { "B", "B2", "B'" },
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

package com.puzzletimer.scramblers;

import java.util.Arrays;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public abstract class BigCubeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private int scrambleLength;
    private Random random;

    public BigCubeRandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        this.scramblerInfo = scramblerInfo;
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }
    
    public abstract int getDimension();
    public abstract String[][] getMoves();

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
            int s = this.random.nextInt(getMoves().length);
            int p = this.random.nextInt(3);

            boolean ignore = false;
            for (int j = i - 1; j >= 0; j--) {
                // if not in the same axis
                if (s / (getDimension() - 1) != slice[j] / (getDimension() - 1)) {
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
            while (i + len < slice.length && slice[i] / (getDimension() - 1) == slice[i + len] / (getDimension() - 1)) {
                len++;
            }

            Arrays.sort(slice, i, i + len);

            i += len;
        }

        String[][] moves = getMoves();

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

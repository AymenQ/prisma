package com.puzzletimer.scramblers;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;


public class MegaminxRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private Random random;

    public MegaminxRandomScrambler(ScramblerInfo scramblerInfo) {
        this.random = new Random();
        this.scramblerInfo = scramblerInfo;
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        String[] sequence = new String[77];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                sequence[11 * i + 2 * j] =
                    this.random.nextInt(2) == 0 ? "R++" : "R--";
                sequence[11 * i + 2 * j + 1] =
                    this.random.nextInt(2) == 0 ? "D++" : "D--";
            }

            sequence[11 * i + 10] = this.random.nextInt(2) == 0 ? "U" : "U'";
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

package com.puzzletimer.scramblers;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;


public class MegaminxRandomScrambler implements Scrambler {
    private Random random;

    public MegaminxRandomScrambler() {
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "MEGAMINX-RANDOM";
    }

    @Override
    public String getPuzzleId() {
        return "MEGAMINX";
    }

    @Override
    public String getDescription() {
        return "Random scrambler";
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

        return new Scramble(UUID.randomUUID(), null, sequence);
    }
}

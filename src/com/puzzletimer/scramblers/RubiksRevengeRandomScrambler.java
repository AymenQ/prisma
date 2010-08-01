package com.puzzletimer.scramblers;

import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;

public class RubiksRevengeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public RubiksRevengeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "4x4x4-CUBE-RANDOM";
    }

    @Override
    public String getPuzzleId() {
        return "4x4x4-CUBE";
    }

    @Override
    public String getDescription() {
        return "Random scrambler";
    }

    @Override
    public Scramble getNextScramble() {
        String[] sequence = new String[this.scrambleLength];
        String[] moves = {
            // X axis
            "R", "R2", "R'", "Rw", "Rw2", "Rw'",
            "L", "L2", "L'", "Lw", "Lw2", "Lw'",

            // Y axis
            "U", "U2", "U'", "Uw", "Uw2", "Uw'",
            "D", "D2", "D'", "Dw", "Dw2", "Dw'",

            // Z axis
            "F", "F2", "F'", "Fw", "Fw2", "Fw'",
            "B", "B2", "B'", "Bw", "Bw2", "Bw'",
        };

        int last = -1;
        for (int i = 0; i < this.scrambleLength; i++)
        {
            int axis = last;
            do {
                axis = this.random.nextInt(3);
            } while (axis == last);
            last = axis;

            sequence[i] = moves[12 * axis + this.random.nextInt(12)];
        }

        return new Scramble(UUID.randomUUID(), null, sequence);
    }
}
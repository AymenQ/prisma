package com.puzzletimer.scramblers;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;

public class RubiksPocketCubeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public RubiksPocketCubeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "2x2x2-CUBE-RANDOM";
    }

    @Override
    public String getPuzzleId() {
        return "2x2x2-CUBE";
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
            "R", "R2", "R'",

            // Y axis
            "U", "U2", "U'",

            // Z axis
            "F", "F2", "F'",
        };

        int last = -1;
        for (int i = 0; i < this.scrambleLength; i++)
        {
            int axis = last;
            do {
                axis = this.random.nextInt(3);
            } while (axis == last);
            last = axis;

            sequence[i] = moves[3 * axis + this.random.nextInt(3)];
        }

        return new Scramble(UUID.randomUUID(), null, sequence);
    }
}

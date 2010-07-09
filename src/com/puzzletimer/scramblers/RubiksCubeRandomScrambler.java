package com.puzzletimer.scramblers;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;

public class RubiksCubeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public RubiksCubeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "RUBIKS-CUBE-RANDOM";
    }

    @Override
    public String getPuzzleId() {
         return "RUBIKS-CUBE";
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
            "R", "R2", "R'", "L", "L2", "L'",

            // Y axis
            "U", "U2", "U'", "D", "D2", "D'",

            // Z axis
            "F", "F2", "F'", "B", "B2", "B'",
        };

        int last = -1;
        for (int i = 0; i < this.scrambleLength; i++)
        {
            int axis = last;
            do {
                axis = this.random.nextInt(3);
            } while (axis == last);
            last = axis;

            sequence[i] = moves[6 * axis + this.random.nextInt(6)];
        }

        return new Scramble(UUID.randomUUID(), null, sequence);
    }
}

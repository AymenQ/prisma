package com.puzzletimer.scramblers;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;

public class PyraminxRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public PyraminxRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "PYRAMINX-RANDOM";
    }

    @Override
    public String getPuzzleId() {
        return "PYRAMINX";
    }

    @Override
    public String getDescription() {
        return "Random scrambler";
    }

    @Override
    public Scramble getNextScramble() {
        ArrayList<String> sequence = new ArrayList<String>();

        String[] moves = {
            "u", "u2",
            "l", "l2",
            "r", "r2",
            "b", "b2",
        };

        for (int i = 0; i < 4 && sequence.size() < this.scrambleLength; i++) {
            int r = this.random.nextInt(3);
            if (r < 2) {
                sequence.add(moves[2 * i + r]);
            }
        }

        String[] moves2 = {
            "U", "U2",
            "L", "L2",
            "R", "R2",
            "B", "B2",
        };

        int last = -1;
        while (sequence.size() < this.scrambleLength) {
            int axis = last;
            do {
                axis = this.random.nextInt(4);
            } while (axis == last);
            last = axis;

            sequence.add(moves2[2 * axis + this.random.nextInt(2)]);
        }

        String[] arraySequence = new String[sequence.size()];
        for (int i = 0; i < sequence.size(); i++) {
            arraySequence[i] = sequence.get(i);
        }

        return new Scramble(UUID.randomUUID(), null, arraySequence);
    }
}

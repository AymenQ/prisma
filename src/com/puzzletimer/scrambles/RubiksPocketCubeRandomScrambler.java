package com.puzzletimer.scrambles;
import java.util.ArrayList;
import java.util.Random;

public class RubiksPocketCubeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public RubiksPocketCubeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    public Scramble getNextScramble() {
        ArrayList<Move> scramble = new ArrayList<Move>();
        Move[] moves = {
            // X axis
            RubiksPocketCubeMove.R,
            RubiksPocketCubeMove.R2,
            RubiksPocketCubeMove.R3,

            // Y axis
            RubiksPocketCubeMove.U,
            RubiksPocketCubeMove.U2,
            RubiksPocketCubeMove.U3,

            // Z axis
            RubiksPocketCubeMove.F,
            RubiksPocketCubeMove.F2,
            RubiksPocketCubeMove.F3,
        };

        int last = -1;
        for (int i = 0; i < scrambleLength; i++)
        {
            int axis = last;
            do {
                axis = random.nextInt(3);
            } while (axis == last);
            last = axis;

            scramble.add(moves[3 * axis + random.nextInt(3)]);
        }

        return new Scramble(scramble);
    }
}

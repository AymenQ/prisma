package com.puzzletimer.scrambles;
import java.util.ArrayList;
import java.util.Random;

public class RubiksCubeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public RubiksCubeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    public Scramble getNextScramble() {
        ArrayList<Move> scramble = new ArrayList<Move>();
        Move[] moves = {
            // X axis
            CubeMove.R, CubeMove.R2, CubeMove.R3,
            CubeMove.L, CubeMove.L2, CubeMove.L3,

            // Y axis
            CubeMove.U, CubeMove.U2, CubeMove.U3,
            CubeMove.D, CubeMove.D2, CubeMove.D3,

            // Z axis
            CubeMove.F, CubeMove.F2, CubeMove.F3,
            CubeMove.B, CubeMove.B2, CubeMove.B3,
        };

        int last = -1;
        for (int i = 0; i < scrambleLength; i++)
        {
            int axis = last;
            do {
                axis = random.nextInt(3);
            } while (axis == last);
            last = axis;

            scramble.add(moves[6 * axis + random.nextInt(6)]);
        }

        return new Scramble(scramble);
    }
}

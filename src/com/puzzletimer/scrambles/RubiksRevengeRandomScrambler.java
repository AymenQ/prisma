package com.puzzletimer.scrambles;

import java.util.ArrayList;
import java.util.Random;

public class RubiksRevengeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;

    public RubiksRevengeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }

    @Override
    public Scramble getNextScramble() {
        ArrayList<Move> scramble = new ArrayList<Move>();
        Move[] moves = {
            // X axis
            CubeMove.R,  CubeMove.R2,  CubeMove.R3,
            CubeMove.Rw, CubeMove.Rw2, CubeMove.Rw3,
            CubeMove.L,  CubeMove.L2,  CubeMove.L3,
            CubeMove.Lw, CubeMove.Lw2, CubeMove.Lw3,

            // Y axis
            CubeMove.U,  CubeMove.U2,  CubeMove.U3,
            CubeMove.Uw, CubeMove.Uw2, CubeMove.Uw3,
            CubeMove.D,  CubeMove.D2,  CubeMove.D3,
            CubeMove.Dw, CubeMove.Dw2, CubeMove.Dw3,

            // Z axis
            CubeMove.F,  CubeMove.F2,  CubeMove.F3,
            CubeMove.Fw, CubeMove.Fw2, CubeMove.Fw3,
            CubeMove.B,  CubeMove.B2,  CubeMove.B3,
            CubeMove.Bw, CubeMove.Bw2, CubeMove.Bw3,
        };

        int last = -1;
        for (int i = 0; i < scrambleLength; i++)
        {
            int axis = last;
            do {
                axis = random.nextInt(3);
            } while (axis == last);
            last = axis;

            scramble.add(moves[12 * axis + random.nextInt(12)]);
        }

        return new Scramble(scramble);
    }
}

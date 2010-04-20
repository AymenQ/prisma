package com.puzzletimer.scrambles;

import java.util.ArrayList;
import java.util.Random;

public class ProfessorsCubeRandomScrambler implements Scrambler {
    private int scrambleLength;
    private Random random;
    
    public ProfessorsCubeRandomScrambler(int scrambleLength) {
        this.scrambleLength = scrambleLength;
        this.random = new Random();
    }
    
    @Override
    public Scramble getNextScramble() {
        ArrayList<Move> scramble = new ArrayList<Move>();
        Move[] moves = {
            // X axis
            RubiksRevengeMove.R,  RubiksRevengeMove.R2,  RubiksRevengeMove.R3,
            RubiksRevengeMove.Rw, RubiksRevengeMove.Rw2, RubiksRevengeMove.Rw3,
            RubiksRevengeMove.L,  RubiksRevengeMove.L2,  RubiksRevengeMove.L3,
            RubiksRevengeMove.Lw, RubiksRevengeMove.Lw2, RubiksRevengeMove.Lw3,
                
            // Y axis
            RubiksRevengeMove.U,  RubiksRevengeMove.U2,  RubiksRevengeMove.U3,
            RubiksRevengeMove.Uw, RubiksRevengeMove.Uw2, RubiksRevengeMove.Uw3,
            RubiksRevengeMove.D,  RubiksRevengeMove.D2,  RubiksRevengeMove.D3,
            RubiksRevengeMove.Dw, RubiksRevengeMove.Dw2, RubiksRevengeMove.Dw3,
                
            // Z axis
            RubiksRevengeMove.F,  RubiksRevengeMove.F2,  RubiksRevengeMove.F3,
            RubiksRevengeMove.Fw, RubiksRevengeMove.Fw2, RubiksRevengeMove.Fw3,
            RubiksRevengeMove.B,  RubiksRevengeMove.B2,  RubiksRevengeMove.B3,
            RubiksRevengeMove.Bw, RubiksRevengeMove.Bw2, RubiksRevengeMove.Bw3,
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

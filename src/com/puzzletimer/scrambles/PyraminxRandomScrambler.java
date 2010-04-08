package com.puzzletimer.scrambles;

import java.util.ArrayList;
import java.util.Random;

public class PyraminxRandomScrambler implements Scrambler {
	private int scrambleLength;
	private Random random;
	
	public PyraminxRandomScrambler(int scrambleLength) {
		this.scrambleLength = scrambleLength;
		this.random = new Random();
	}
	
	@Override
	public Scramble getNextScramble() {
		ArrayList<Move> scramble = new ArrayList<Move>();

		Move[] moves = {
			PyraminxMove.u, PyraminxMove.u2,
			PyraminxMove.l, PyraminxMove.l2,
			PyraminxMove.r, PyraminxMove.r2,
			PyraminxMove.b, PyraminxMove.b2,			
		};

		for (int i = 0; i < 4 && scramble.size() < scrambleLength; i++) {
			int r = random.nextInt(3); 
			if (r < 2) {
				scramble.add(moves[2 * i + r]);
			}
		}
		
		Move[] moves2 = {
			PyraminxMove.U, PyraminxMove.U2,
			PyraminxMove.L, PyraminxMove.L2,
			PyraminxMove.R, PyraminxMove.R2,
			PyraminxMove.B, PyraminxMove.B2,			
		};

        int last = -1;
        while (scramble.size() < scrambleLength) {
            int axis = last;
            do {
                axis = random.nextInt(4);
            } while (axis == last);
            last = axis;

            scramble.add(moves2[2 * axis + random.nextInt(2)]);
        }

		return new Scramble(scramble);
	}
}

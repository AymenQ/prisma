package com.puzzletimer.scrambles;
import java.util.ArrayList;
import java.util.Random;


public class RandomScrambler implements Scrambler {
	private int scrambleLength;
	private Random random;
	
	public RandomScrambler(int scrambleLength) {
		this.scrambleLength = scrambleLength;
		this.random = new Random();
	}
	
	public Scramble getNextScramble() {
		ArrayList<Integer> scramble = new ArrayList<Integer>();
		int[] moves = {
			// X axis
			Move.R, Move.R2, Move.R3, Move.L, Move.L2, Move.L3,
			
			// Y axis
			Move.U, Move.U2, Move.U3, Move.D, Move.D2, Move.D3,
			
			// Z axis
			Move.F, Move.F2, Move.F3, Move.B, Move.B2, Move.B3,
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

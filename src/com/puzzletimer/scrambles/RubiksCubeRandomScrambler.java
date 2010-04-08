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
			RubiksCubeMove.R, RubiksCubeMove.R2, RubiksCubeMove.R3,
			RubiksCubeMove.L, RubiksCubeMove.L2, RubiksCubeMove.L3,
			
			// Y axis
			RubiksCubeMove.U, RubiksCubeMove.U2, RubiksCubeMove.U3,
			RubiksCubeMove.D, RubiksCubeMove.D2, RubiksCubeMove.D3,
			
			// Z axis
			RubiksCubeMove.F, RubiksCubeMove.F2, RubiksCubeMove.F3,
			RubiksCubeMove.B, RubiksCubeMove.B2, RubiksCubeMove.B3,
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

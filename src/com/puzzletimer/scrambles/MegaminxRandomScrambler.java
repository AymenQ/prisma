package com.puzzletimer.scrambles;
import java.util.ArrayList;
import java.util.Random;

public class MegaminxRandomScrambler implements Scrambler {
	private Random random;
	
	public MegaminxRandomScrambler() {
		this.random = new Random();
	}
	
	public Scramble getNextScramble() {
		ArrayList<Move> scramble = new ArrayList<Move>();
		
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 5; j++) {
				scramble.add(random.nextInt(2) == 0 ?
					MegaminxMove.R2 :
					MegaminxMove.R7);

				scramble.add(random.nextInt(2) == 0 ?
					MegaminxMove.D2 :
					MegaminxMove.D7);
			}
			
			scramble.add(random.nextInt(2) == 0 ?
				MegaminxMove.U :
				MegaminxMove.U6);
		}

		return new Scramble(scramble);
	}
}

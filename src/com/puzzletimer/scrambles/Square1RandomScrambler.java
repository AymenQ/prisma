package com.puzzletimer.scrambles;

import java.util.ArrayList;

public class Square1RandomScrambler implements Scrambler {
	public Square1RandomScrambler(int scrambleLength) {
	}
	
	@Override
	public Scramble getNextScramble() {
		ArrayList<Move> moves = new ArrayList<Move>();
		moves.add(new Square1Move(0,5));
		moves.add(new Square1Move(0,-3));
		moves.add(new Square1Move(-5,4));
		moves.add(new Square1Move(5,0));
		moves.add(new Square1Move(0,3));
		moves.add(new Square1Move(1,0));
		moves.add(new Square1Move(2,2));
		moves.add(new Square1Move(4,4));
		moves.add(new Square1Move(2,3));
		moves.add(new Square1Move(-3,2));
		moves.add(new Square1Move(0,1));
		moves.add(new Square1Move(6,4));
		moves.add(new Square1Move(-2,0));
		moves.add(new Square1Move(0,1));
		moves.add(new Square1Move(0,3));
		moves.add(new Square1Move(6,5));
		moves.add(new Square1Move(-3,0));
		
		return new Scramble(moves);
	}
}

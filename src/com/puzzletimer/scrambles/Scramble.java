package com.puzzletimer.scrambles;

import java.util.ArrayList;

public class Scramble {
	public ArrayList<Integer> moves;
	
	public Scramble(ArrayList<Integer> moves) {
		this.moves = moves;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < moves.size() - 1; i++) {
			sb.append(Move.toString(moves.get(i)) + "  ");
		}
		sb.append(Move.toString(moves.get(moves.size() - 1)));

		return sb.toString();
	}
}

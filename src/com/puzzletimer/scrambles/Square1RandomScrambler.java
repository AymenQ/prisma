package com.puzzletimer.scrambles;

import java.util.ArrayList;
import java.util.Random;

public class Square1RandomScrambler implements Scrambler {
	private int scrambleLength;
	
	public Square1RandomScrambler(int scrambleLength) {
		this.scrambleLength = scrambleLength;
	}
	
	private boolean[] rotateClockwise(boolean[] xs, int n) {
		boolean[] ys = new boolean[xs.length];
		for (int i = 0; i < xs.length; i++) {
			ys[i] = xs[(i + n) % ys.length];
		}
		return ys;
	}
	
	@Override
	public Scramble getNextScramble() {
		boolean t = true, f = false;
		boolean[] top = new boolean[] { t, t, f, t, t, f, t, t, f, t, t, f };
		boolean[] bottom = new boolean[] { t, f, t, t, f, t, t, f, t, t, f, t };
		
		Random r = new Random();
		
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < scrambleLength; i++) {
			ArrayList<Integer> choices;
			int k;
			
			// top
			choices = new ArrayList<Integer>();
			k = 0;
			for (int j = 0; j < 12; j++) {
				if (top[j] && top[(j + 6) % 12]) {
					choices.add(j);
					k++;
				}
			}
			
			int x = choices.get(r.nextInt(choices.size()));
			top = rotateClockwise(top, x);
			
			// bottom
			choices = new ArrayList<Integer>();
			k = 0;
			for (int j = 0; j < 12; j++) {
				if (bottom[j] && bottom[(j + 6) % 12]) {
					choices.add(j);
					k++;
				}
			}
			
			int y = choices.get(r.nextInt(choices.size()));
			bottom = rotateClockwise(bottom, y);
			
			// right
			boolean[] newTop = new boolean[12]; 
			boolean[] newBottom = new boolean[12];
			
			for (int j = 0; j < 12; j++) {
				if (j < 7) {
					newTop[j] = top[j];
					newBottom[j] = bottom[j];
				} else {
					newTop[j] = bottom[j];
					newBottom[j] = top[j];
				}
			}
			
			top = newTop;
			bottom = newBottom;
			
			moves.add(new Square1Move(x <= 6 ? x : x - 12, y <= 6 ? y : y - 12));
		}
		
		return new Scramble(moves);
	}
}

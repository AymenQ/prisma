package com.puzzletimer.scrambles;

public class Move {
	public static final Integer X  =  0;
	public static final Integer X2 =  1;
	public static final Integer X3 =  2;
	public static final Integer Y  =  3;
	public static final Integer Y2 =  4;
	public static final Integer Y3 =  5;
	public static final Integer Z  =  6;
	public static final Integer Z2 =  7;
	public static final Integer Z3 =  8;
	public static final Integer F  =  9;
	public static final Integer F2 = 10;
	public static final Integer F3 = 11;
	public static final Integer R  = 12;
	public static final Integer R2 = 13;
	public static final Integer R3 = 14;
	public static final Integer B  = 15;
	public static final Integer B2 = 16;
	public static final Integer B3 = 17;
	public static final Integer L  = 18;
	public static final Integer L2 = 19;
	public static final Integer L3 = 20;
	public static final Integer U  = 21;
	public static final Integer U2 = 22;
	public static final Integer U3 = 23;
	public static final Integer D  = 24;
	public static final Integer D2 = 25;
	public static final Integer D3 = 26;
	
	public static String toString(Integer move) {
		return new String[] {
			"x", "x2", "x'",
			"y", "y2", "y'",
			"z", "z2", "z'",
			"F", "F2", "F'",
			"R", "R2", "R'",
			"B", "B2", "B'",
			"L", "L2", "L'",
			"U", "U2", "U'",
			"D", "D2", "D'",
		}[move];
	}
}

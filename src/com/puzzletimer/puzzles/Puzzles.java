package com.puzzletimer.puzzles;

import java.util.HashMap;

public class Puzzles {
    private static HashMap<String, Puzzle> puzzles;

    static {
        puzzles = new HashMap<String, Puzzle>();
        puzzles.put("EMPTY", new Empty());
        puzzles.put("2x2x2-CUBE", new RubiksPocketCube());
        puzzles.put("RUBIKS-CUBE", new RubiksCube());
        puzzles.put("4x4x4-CUBE", new RubiksRevenge());
        puzzles.put("5x5x5-CUBE", new ProfessorsCube());
        puzzles.put("MEGAMINX", new Megaminx());
        puzzles.put("PYRAMINX", new Pyraminx());
        puzzles.put("SQUARE-1", new Square1());
    }

    public static Puzzle getPuzzle(String puzzleId) {
        return puzzles.get(puzzleId);
    }
}

package com.puzzletimer.puzzles;

import java.util.HashMap;

public class PuzzleBuilder {
    private static Puzzle[] puzzles;
    private static HashMap<String, Puzzle> puzzleMap;

    static {
        puzzles = new Puzzle[] {
            new Other(),
            new RubiksPocketCube(),
            new RubiksCube(),
            new RubiksRevenge(),
            new ProfessorsCube(),
            new VCube6(),
            new VCube7(),
            new Megaminx(),
            new Pyraminx(),
            new Square1(),
            new RubiksClock(),
        };

        puzzleMap = new HashMap<String, Puzzle>();
        for (Puzzle puzzle : puzzles) {
            puzzleMap.put(puzzle.getPuzzleInfo().getPuzzleId(), puzzle);
        }
    }

    public static Puzzle getPuzzle(String puzzleId) {
        return puzzleMap.get(puzzleId);
    }

    public static Puzzle[] getPuzzles() {
        return puzzles;
    }
}

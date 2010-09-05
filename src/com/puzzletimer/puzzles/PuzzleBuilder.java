package com.puzzletimer.puzzles;

import java.util.HashMap;

public class PuzzleBuilder {
    private static HashMap<String, Puzzle> puzzleMap;

    static {
        Puzzle[] puzzles = {
            new Other(),
            new RubiksPocketCube(),
            new RubiksCube(),
            new RubiksRevenge(),
            new ProfessorsCube(),
            new Megaminx(),
            new Pyraminx(),
            new Square1(),
        };

        puzzleMap = new HashMap<String, Puzzle>();
        for (Puzzle puzzle : puzzles) {
            puzzleMap.put(puzzle.getPuzzleInfo().getPuzzleId(), puzzle);
        }
    }

    public static Puzzle getPuzzle(String puzzleId) {
        return puzzleMap.get(puzzleId);
    }
}

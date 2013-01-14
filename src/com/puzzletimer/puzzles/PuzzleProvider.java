package com.puzzletimer.puzzles;

import java.util.HashMap;

public class PuzzleProvider {
    private Puzzle[] puzzles;
    private HashMap<String, Puzzle> puzzleMap;

    public PuzzleProvider() {
        this.puzzles = new Puzzle[] {
            new RubiksPocketCube(),
            new RubiksCube(),
            new RubiksRevenge(),
            new ProfessorsCube(),
            new VCube6(),
            new VCube7(),
            new SS8(),
            new SS9(),
            new RubiksClock(),
            new Megaminx(),
            new Pyraminx(),
            new Square1(),
            new Skewb(),
            new FloppyCube(),
            new TowerCube(),
            new RubiksTower(),
            new RubiksDomino(),
            new Other(),
        };

        this.puzzleMap = new HashMap<String, Puzzle>();
        for (Puzzle puzzle : this.puzzles) {
            this.puzzleMap.put(puzzle.getPuzzleInfo().getPuzzleId(), puzzle);
        }
    }

    public Puzzle[] getAll() {
        return this.puzzles;
    }

    public Puzzle get(String puzzleId) {
        return this.puzzleMap.get(puzzleId);
    }
}

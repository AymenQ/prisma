package com.puzzletimer.models;

public class PuzzleInfo {
    private String puzzleId;
    private String description;

    public PuzzleInfo(String puzzleId, String description) {
        this.puzzleId = puzzleId;
        this.description = description;
    }

    public String getPuzzleId() {
        return this.puzzleId;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}

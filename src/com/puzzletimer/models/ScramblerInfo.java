package com.puzzletimer.models;

public class ScramblerInfo {
    private final String scramblerId;
    private final String puzzleId;
    private final String description;

    public ScramblerInfo(String scramblerId, String puzzleId, String description) {
        this.scramblerId = scramblerId;
        this.puzzleId = puzzleId;
        this.description = description;
    }

    public String getScramblerId() {
        return this.scramblerId;
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

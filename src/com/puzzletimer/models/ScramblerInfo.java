package com.puzzletimer.models;

public class ScramblerInfo {
    private String scramblerId;
    private String puzzleId;
    private String description;

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

package com.puzzletimer.models;

import java.util.UUID;

public class Solution {
    private UUID solutionId;
    private UUID categoryId;
    private Scramble scramble;
    private Timing timing;
    private String penalty;

    public Solution(UUID solutionId, UUID categoryId, Scramble scramble, Timing timing, String penalty) {
        this.solutionId = solutionId;
        this.categoryId = categoryId;
        this.scramble = scramble;
        this.timing = timing;
        this.penalty = penalty;
    }

    public UUID getSolutionId() {
        return this.solutionId;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public Scramble getScramble() {
        return this.scramble;
    }

    public Timing getTiming() {
        return this.timing;
    }

    public Solution setTiming(Timing timing) {
        return new Solution(
            this.solutionId,
            this.categoryId,
            this.scramble,
            timing,
            this.penalty);
    }

    public String getPenalty() {
        return this.penalty;
    }

    public Solution setPenalty(String penalty) {
        return new Solution(
            this.solutionId,
            this.categoryId,
            this.scramble,
            this.timing,
            penalty);
    }
}

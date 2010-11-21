package com.puzzletimer.models;

import java.util.UUID;

public class Solution {
    private UUID solutionId;
    private UUID categoryId;
    private Scramble scramble;
    public Timing timing;
    public String penalty;

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
}

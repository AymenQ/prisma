package com.puzzletimer.models;

import java.util.UUID;

public class Solution {
    private UUID solutionId;
    private UUID scrambleId;
    public Timing timing;
    public String penalty;

    public Solution(UUID solutionId, UUID scrambleId, Timing timing, String penalty) {
        this.solutionId = solutionId;
        this.scrambleId = scrambleId;
        this.timing = timing;
        this.penalty = penalty;
    }

    public UUID getSolutionId() {
        return this.solutionId;
    }

    public UUID getScrambleId() {
        return this.scrambleId;
    }
}

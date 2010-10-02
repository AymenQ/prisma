package com.puzzletimer.models;

import java.util.UUID;

public class Solution {
    private UUID solutionId;
    private UUID scrambleId;
    private Timing timing;
    private String penalty;

    public Solution(UUID solutionId, UUID scrambleId, Timing timing, String penalty) {
        super();
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

    public Timing getTiming() {
        return this.timing;
    }

    public String getPenalty() {
        return this.penalty;
    }
}

package com.puzzletimer.models;

import java.util.UUID;

public class Solution {
    private UUID solutionId;
    private UUID categoryId;
    private Timing timing;
    private String penalty;

    public Solution(UUID solutionId, UUID categoryId, Timing timing, String penalty) {
        super();
        this.solutionId = solutionId;
        this.categoryId = categoryId;
        this.timing = timing;
        this.penalty = penalty;
    }

    public UUID getSolutionId() {
        return this.solutionId;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public Timing getTiming() {
        return this.timing;
    }

    public String getPenalty() {
        return this.penalty;
    }
}

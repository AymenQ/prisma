package com.puzzletimer.models;

import java.util.UUID;

public class Solution {
    private final UUID solutionId;
    private final UUID categoryId;
    private final Scramble scramble;
    private final Timing timing;
    private final String penalty;
    private final String comment;

    public Solution(UUID solutionId, UUID categoryId, Scramble scramble, Timing timing, String penalty, String comment) {
        this.solutionId = solutionId;
        this.categoryId = categoryId;
        this.scramble = scramble;
        this.timing = timing;
        this.penalty = penalty;
        this.comment = comment;
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
            this.penalty,
            this.comment);
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
            penalty,
            this.comment);
    }

    public String getComment () { return this.comment; }

    public Solution setComment(String comment) {
        return new Solution(
                this.solutionId,
                this.categoryId,
                this.scramble,
                this.timing,
                this.penalty,
                comment);
    }
}

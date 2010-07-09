package com.puzzletimer.models;

import java.util.UUID;

public class Scramble {
    private UUID scrambleId;
    private UUID categoryId;
    private String[] sequence;

    public Scramble(UUID scrambleId, UUID categoryId, String[] sequence) {
        this.scrambleId = scrambleId;
        this.categoryId = categoryId;
        this.sequence = sequence;
    }

    public UUID getScramblerId() {
        return this.scrambleId;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String[] getSequence() {
        return this.sequence;
    }
}

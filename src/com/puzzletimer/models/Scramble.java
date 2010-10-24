package com.puzzletimer.models;

import java.util.UUID;

public class Scramble {
    private UUID scrambleId;
    private String scramblerId;
    private String[] sequence;

    public Scramble(UUID scrambleId, String scramblerId, String[] sequence) {
        this.scrambleId = scrambleId;
        this.scramblerId = scramblerId;
        this.sequence = sequence;
    }

    public UUID getScrambleId() {
        return this.scrambleId;
    }

    public String getScramblerId() {
        return this.scramblerId;
    }

    public String[] getSequence() {
        return this.sequence;
    }
}

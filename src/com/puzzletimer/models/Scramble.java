package com.puzzletimer.models;

public class Scramble {
    private final String scramblerId;
    private final String[] sequence;

    public Scramble(String scramblerId, String[] sequence) {
        this.scramblerId = scramblerId;
        this.sequence = sequence;
    }

    public String getScramblerId() {
        return this.scramblerId;
    }

    public String[] getSequence() {
        return this.sequence;
    }
}

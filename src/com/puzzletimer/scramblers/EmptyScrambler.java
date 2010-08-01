package com.puzzletimer.scramblers;

import java.util.UUID;

import com.puzzletimer.models.Scramble;

public class EmptyScrambler implements Scrambler {
    @Override
    public String getScramblerId() {
        return "EMPTY";
    }

    @Override
    public String getPuzzleId() {
        return "EMPTY";
    }

    @Override
    public String getDescription() {
        return "Empty scrambler";
    }

    @Override
    public Scramble getNextScramble() {
        return new Scramble(UUID.randomUUID(), null, new String[] { });
    }
}

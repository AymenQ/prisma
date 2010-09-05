package com.puzzletimer.scramblers;

import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public class EmptyScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;

    public EmptyScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble(UUID scrambleId, UUID categoryId) {
        return new Scramble(scrambleId, categoryId, new String[] { });
    }
}

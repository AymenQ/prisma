package com.puzzletimer.scramblers;

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
    public Scramble getNextScramble() {
        return new Scramble(
            getScramblerInfo().getScramblerId(),
            new String[] { });
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

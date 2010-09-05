package com.puzzletimer.scramblers;

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
    public String[] getNextScrambleSequence() {
        return new String[] { };
    }
}

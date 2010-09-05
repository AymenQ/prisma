package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;

public interface Scrambler {
    ScramblerInfo getScramblerInfo();
    String[] getNextScrambleSequence();
}

package com.puzzletimer.scramblers;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public interface Scrambler {
    ScramblerInfo getScramblerInfo();
    Scramble getNextScramble();
}

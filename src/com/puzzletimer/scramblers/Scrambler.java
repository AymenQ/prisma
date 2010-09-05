package com.puzzletimer.scramblers;

import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;

public interface Scrambler {
    ScramblerInfo getScramblerInfo();
    Scramble getNextScramble(UUID scrambleId, UUID categoryInfo);
}

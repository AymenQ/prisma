package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.MegaminxPuzzle;

public class MegaminxWcaScrambler extends WcaScrambler {
    public MegaminxWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new MegaminxPuzzle());
    }
}

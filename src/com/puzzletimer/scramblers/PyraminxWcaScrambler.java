package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.PyraminxPuzzle;

public class PyraminxWcaScrambler extends WcaScrambler {
    public PyraminxWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new PyraminxPuzzle());
    }
}

package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.SkewbPuzzle;

public class SkewbWcaScrambler extends WcaScrambler {
    public SkewbWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new SkewbPuzzle());
    }
}

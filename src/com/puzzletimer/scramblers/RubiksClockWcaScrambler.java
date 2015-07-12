package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.ClockPuzzle;

public class RubiksClockWcaScrambler extends WcaScrambler {
    public RubiksClockWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new ClockPuzzle());
    }
}

package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.FourByFourRandomTurnsCubePuzzle;

public class RubiksRevengeWcaScrambler extends WcaScrambler {
    public RubiksRevengeWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new FourByFourRandomTurnsCubePuzzle());
    }
}

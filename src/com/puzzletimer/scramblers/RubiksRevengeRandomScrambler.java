package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;

public class RubiksRevengeRandomScrambler extends BigCubeRandomScrambler {
    public RubiksRevengeRandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        super(scramblerInfo, scrambleLength);
    }

    public int getDimension() {
        return 4;
    }

    public String[][] getMoves() {
        String[][] moves = {
            { "U", "U2", "U'" }, { "Uw", "Uw2", "Uw'" }, { "D", "D2", "D'" },
            { "L", "L2", "L'" }, { "Rw", "Rw2", "Rw'" }, { "R", "R2", "R'" },
            { "F", "F2", "F'" }, { "Fw", "Fw2", "Fw'" }, { "B", "B2", "B'" },
        };
        return moves;
    }
}

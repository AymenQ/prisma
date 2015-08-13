package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;

public class ProfessorsCubeRandomScrambler extends BigCubeRandomScrambler {
    public ProfessorsCubeRandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        super(scramblerInfo, scrambleLength);
    }

    public int getDimension() {
        return 5;
    }

    public String[][] getMoves() {
        return new String[][]{
            { "U", "U2", "U'" }, { "Uw", "Uw2", "Uw'" },
            { "Dw", "Dw2", "Dw'" }, { "D", "D2", "D'" },
            { "L", "L2", "L'" }, { "Lw", "Lw2", "Lw'" },
            { "Rw", "Rw2", "Rw'" }, { "R", "R2", "R'" },
            { "F", "F2", "F'" }, { "Fw", "Fw2", "Fw'" },
            { "Bw", "Bw2", "Bw'" }, { "B", "B2", "B'" },
        };
    }
}



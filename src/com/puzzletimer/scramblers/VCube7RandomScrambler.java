package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;

public class VCube7RandomScrambler extends BigCubeRandomScrambler {
    
    public VCube7RandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        super(scramblerInfo, scrambleLength);
    }

    public int getDimension() {
        return 7;
    }

    public String[][] getMoves() {
        return new String[][]{
            { "U", "U2", "U'" }, { "2U", "2U2", "2U'" }, { "3U", "3U2", "3U'" },
            { "3D", "3D2", "3D'" }, { "2D", "2D2", "2D'" }, { "D", "D2", "D'" },
            { "L", "L2", "L'" }, { "2L", "2L2", "2L'" }, { "3L", "3L2", "3L'" },
            { "3R", "3R2", "3R'" }, { "2R", "2R2", "2R'" }, { "R", "R2", "R'" },
            { "F", "F2", "F'" }, { "2F", "2F2", "2F'" }, { "3F", "3F2", "3F'" },
            { "3B", "3B2", "3B'" }, { "2B", "2B2", "2B'" }, { "B", "B2", "B'" },
        };
    }
}

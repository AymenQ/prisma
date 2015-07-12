package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;

public class SS9RandomScrambler extends BigCubeRandomScrambler {

    public SS9RandomScrambler(ScramblerInfo scramblerInfo, int scrambleLength) {
        super(scramblerInfo, scrambleLength);
    }

    public int getDimension() {
        return 9;
    }

    public String[][] getMoves() {
        return new String[][]{
            { "U", "U2", "U'" }, { "2U", "2U2", "2U'" }, { "3U", "3U2", "3U'" }, { "4U", "4U2", "4U'" },
            { "D", "D2", "D'" }, { "2D", "2D2", "2D'" }, { "3D", "3D2", "3D'" }, { "4D", "4D2", "4D'" },
            { "L", "L2", "L'" }, { "2L", "2L2", "2L'" }, { "3L", "3L2", "3L'" }, { "4L", "4L2", "4L'" },
            { "R", "R2", "R'" }, { "2R", "2R2", "2R'" }, { "3R", "3R2", "3R'" }, { "4R", "4R2", "4R'" },
            { "F", "F2", "F'" }, { "2F", "2F2", "2F'" }, { "3F", "3F2", "3F'" }, { "4F", "4F2", "4F'" },
            { "B", "B2", "B'" }, { "2B", "2B2", "2B'" }, { "3B", "3B2", "3B'" }, { "4B", "4B2", "4B'" },
        };
    }
}

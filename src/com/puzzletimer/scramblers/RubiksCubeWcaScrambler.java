package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.ThreeByThreeCubePuzzle;

public class RubiksCubeWcaScrambler extends WcaScrambler {
    public RubiksCubeWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new ThreeByThreeCubePuzzle());
    }
}

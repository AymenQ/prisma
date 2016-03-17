package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.CubePuzzle;

public class VCube6WcaScrambler extends WcaScrambler {
    public VCube6WcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new CubePuzzle(6));
    }
}

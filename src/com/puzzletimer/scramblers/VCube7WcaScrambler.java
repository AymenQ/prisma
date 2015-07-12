package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.CubePuzzle;

public class VCube7WcaScrambler extends WcaScrambler {
    public VCube7WcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new CubePuzzle(7));
    }
}

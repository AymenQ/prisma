package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.TwoByTwoCubePuzzle;

public class RubiksPocketCubeWcaScramber extends WcaScrambler {
    public RubiksPocketCubeWcaScramber(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new TwoByTwoCubePuzzle());
    }
}

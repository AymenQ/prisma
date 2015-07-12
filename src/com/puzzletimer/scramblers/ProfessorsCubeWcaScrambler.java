package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.CubePuzzle;

public class ProfessorsCubeWcaScrambler extends WcaScrambler {
    public ProfessorsCubeWcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new CubePuzzle(5));
    }
}

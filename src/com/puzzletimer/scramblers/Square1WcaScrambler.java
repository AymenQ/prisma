package com.puzzletimer.scramblers;

import com.puzzletimer.models.ScramblerInfo;
import puzzle.SquareOnePuzzle;

public class Square1WcaScrambler extends WcaScrambler {
    public Square1WcaScrambler(ScramblerInfo scramblerInfo) {
        super(scramblerInfo);
        setPuzzle(new SquareOnePuzzle());
    }
}

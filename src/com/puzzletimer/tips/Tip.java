package com.puzzletimer.tips;

import com.puzzletimer.models.Scramble;

public interface Tip {
    String getTipId();
    String getPuzzleId();
    String getTipDescription();
    String getTip(Scramble scramble);
}

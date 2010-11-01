package com.puzzletimer.tips;

import com.puzzletimer.models.Scramble;

public interface Tipper {
    String getPuzzleId();
    String getTips(Scramble scramble);
}

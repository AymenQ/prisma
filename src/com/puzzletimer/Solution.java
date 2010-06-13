package com.puzzletimer;

import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.timer.Timing;

public class Solution {
    private Scramble scramble;
    private Timing timing;

    public Solution(Scramble scramble, Timing timing) {
        this.scramble = scramble;
        this.timing = timing;
    }

    public Scramble getScramble() {
        return this.scramble;
    }

    public Timing getTiming() {
        return this.timing;
    }
}

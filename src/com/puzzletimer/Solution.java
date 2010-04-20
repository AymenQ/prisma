package com.puzzletimer;

import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.timer.Timer;

public class Solution {
    private Scramble scramble;
    private Timer timer;
    
    public Solution(Scramble scramble, Timer timer) {
        this.scramble = scramble;
        this.timer = timer;
    }

    public Scramble getScramble() {
        return scramble;
    }

    public void setScramble(Scramble scramble) {
        this.scramble = scramble;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}

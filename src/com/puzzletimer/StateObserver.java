package com.puzzletimer;

import java.util.ArrayList;

import com.puzzletimer.scrambles.Scramble;

public abstract class StateObserver {
    public void updateScramble(Puzzle puzzle, Scramble scramble) {
    }

    public void updateSolutions(ArrayList<Solution> solutions) {
    }
}

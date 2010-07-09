package com.puzzletimer;

import java.util.ArrayList;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.puzzles.Puzzle;

public abstract class StateObserver {
    public void updateScramble(Puzzle puzzle, Scramble scramble) {
    }

    public void updateSolutions(ArrayList<Solution> solutions) {
    }
}

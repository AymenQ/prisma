package com.puzzletimer;

import java.util.ArrayList;

import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.timer.Timing;

public class State {
    private ArrayList<StateObserver> observers;
    private ArrayList<Solution> solutions;
    private Puzzle puzzle;
    private Scramble scramble;

    public State(Puzzle puzzle) {
        this.observers = new ArrayList<StateObserver>();
        this.solutions = new ArrayList<Solution>();
        this.puzzle = puzzle;
        this.scramble = this.puzzle.getScrambler().getNextScramble();
    }

    public void addStateObserver(StateObserver observer) {
        this.observers.add(observer);
    }

    public void notifyScrambleObservers() {
        for (StateObserver observer : this.observers) {
            observer.updateScramble(this.puzzle, this.scramble);
        }
    }

    public void notifySolutionsObservers() {
        for (StateObserver observer : this.observers) {
            observer.updateSolutions(this.solutions);
        }
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;

        this.solutions = new ArrayList<Solution>();
        notifySolutionsObservers();

        this.scramble = this.puzzle.getScrambler().getNextScramble();
        notifyScrambleObservers();
    }

    public void addSolution(Timing timing) {
        this.solutions.add(new Solution(this.scramble, timing));
        notifySolutionsObservers();

        this.scramble = this.puzzle.getScrambler().getNextScramble();
        notifyScrambleObservers();
    }

    public void removeSolution(int i) {
        this.solutions.remove(i);
        notifySolutionsObservers();
    }
}

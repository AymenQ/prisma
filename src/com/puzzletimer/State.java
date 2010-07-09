package com.puzzletimer;

import java.util.ArrayList;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.Puzzles;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.Scramblers;

public class State {
    private ArrayList<StateObserver> observers;
    private ArrayList<Solution> solutions;
    private Category category;
    private Scrambler scrambler;
    private Puzzle puzzle;
    private Scramble scramble;

    public State(Category category) {
        this.observers = new ArrayList<StateObserver>();
        this.solutions = new ArrayList<Solution>();
        this.category = category;
        this.scrambler = Scramblers.getScrambler(this.category.getScramblerId());
        this.puzzle = Puzzles.getPuzzle(this.scrambler.getPuzzleId());
        this.scramble = this.scrambler.getNextScramble();
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

    public void setCategory(Category category) {
        this.category = category;
        this.scrambler = Scramblers.getScrambler(this.category.getScramblerId());
        this.puzzle = Puzzles.getPuzzle(this.scrambler.getPuzzleId());

        this.solutions = new ArrayList<Solution>();
        notifySolutionsObservers();

        this.scramble = this.scrambler.getNextScramble();
        notifyScrambleObservers();
    }

    public void addSolution(Timing timing) {
        this.solutions.add(new Solution(UUID.randomUUID(), this.category.getCategoryId(), timing, ""));
        notifySolutionsObservers();

        this.scramble = this.scrambler.getNextScramble();
        notifyScrambleObservers();
    }

    public void removeSolution(int i) {
        this.solutions.remove(i);
        notifySolutionsObservers();
    }
}

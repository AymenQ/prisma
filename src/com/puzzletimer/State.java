package com.puzzletimer;

import java.util.ArrayList;
import java.util.UUID;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.puzzles.Puzzle;
import com.puzzletimer.puzzles.PuzzleBuilder;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerBuilder;

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
        this.scrambler = ScramblerBuilder.getScrambler(this.category.getScramblerId());
        this.puzzle = PuzzleBuilder.getPuzzle(this.scrambler.getScramblerInfo().getPuzzleId());
        this.scramble = new Scramble(UUID.randomUUID(), category.getCategoryId(), this.scrambler.getNextScrambleSequence());
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
        this.scrambler = ScramblerBuilder.getScrambler(this.category.getScramblerId());
        this.puzzle = PuzzleBuilder.getPuzzle(this.scrambler.getScramblerInfo().getPuzzleId());

        this.solutions = new ArrayList<Solution>();
        notifySolutionsObservers();

        this.scramble = new Scramble(UUID.randomUUID(), category.getCategoryId(), this.scrambler.getNextScrambleSequence());
        notifyScrambleObservers();
    }

    public void addSolution(Timing timing) {
        this.solutions.add(new Solution(UUID.randomUUID(), this.category.getCategoryId(), timing, ""));
        notifySolutionsObservers();

        this.scramble = new Scramble(UUID.randomUUID(), this.category.getCategoryId(), this.scrambler.getNextScrambleSequence());
        notifyScrambleObservers();
    }

    public void removeSolution(int i) {
        this.solutions.remove(i);
        notifySolutionsObservers();
    }
}

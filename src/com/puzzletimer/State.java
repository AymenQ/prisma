package com.puzzletimer;

import java.util.ArrayList;

import com.puzzletimer.timer.Timer;

public class State {
	private ArrayList<StateObserver> observers;
	private Puzzle puzzle;
	private ArrayList<Solution> solutions;
	private Solution currentSolution;

	public State(Puzzle puzzle) {
		this.observers = new ArrayList<StateObserver>();
		this.puzzle = puzzle;
		this.solutions = new ArrayList<Solution>();
		this.currentSolution = new Solution(this.puzzle.getScrambler().getNextScramble(), new Timer());
	}

	public void addStateObserver(StateObserver observer) {
		this.observers.add(observer);
	}

	public void notifyScrambleObservers() {
		for (StateObserver observer : this.observers) {
			observer.updateScramble(this.puzzle, this.currentSolution.getScramble());
		}
	}

	public void notifySolutionsObservers() {
		for (StateObserver observer : this.observers) {
			observer.updateSolutions(this.solutions);
		}
	}
	
	public void setPuzzle(Puzzle puzzle) {
		if (this.currentSolution.getTimer().isRunning()) {
			this.currentSolution.getTimer().stop();
			for (StateObserver observer : this.observers) {
				observer.onSolutionEnd(this.currentSolution);
			}
		}
		
		this.puzzle = puzzle;

		this.solutions = new ArrayList<Solution>();
		notifySolutionsObservers();
		
		this.currentSolution = new Solution(this.puzzle.getScrambler().getNextScramble(), new Timer());
		notifyScrambleObservers();
	}
	
	public void startCurrentSolution() {
		this.currentSolution.getTimer().start();
		for (StateObserver observer : this.observers) {
			observer.onSolutionBegin(this.currentSolution);
		}
	}
	
	public void stopCurrentSolution() {
		this.currentSolution.getTimer().stop();
		for (StateObserver observer : this.observers) {
			observer.onSolutionEnd(this.currentSolution);
		}

		this.solutions.add(this.currentSolution);
		notifySolutionsObservers();

		this.currentSolution = new Solution(this.puzzle.getScrambler().getNextScramble(), new Timer());
		notifyScrambleObservers();
	}

	public void removeSolution(int i) {
		solutions.remove(i);
		notifySolutionsObservers();
	}
}

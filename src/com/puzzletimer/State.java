package com.puzzletimer;

import java.util.ArrayList;

import com.puzzletimer.scrambles.Scrambler;
import com.puzzletimer.timer.Timer;

public class State {
	private ArrayList<StateObserver> observers;
	private Scrambler scrambler;
	private ArrayList<Solution> solutions;
	private Solution currentSolution;

	public State(Scrambler scrambler) {
		this.observers = new ArrayList<StateObserver>();
		this.scrambler = scrambler;
		this.solutions = new ArrayList<Solution>();
		this.currentSolution = new Solution(scrambler.getNextScramble(), new Timer());
	}

	public void addStateObserver(StateObserver observer) {
		this.observers.add(observer);
	}

	public void notifyScrambleObservers() {
		for (StateObserver observer : this.observers) {
			observer.updateScramble(this.currentSolution.getScramble());
		}
	}

	public void notifySolutionsObservers() {
		for (StateObserver observer : this.observers) {
			observer.updateSolutions(this.solutions);
		}
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

		this.currentSolution = new Solution(scrambler.getNextScramble(), new Timer());
		notifyScrambleObservers();
	}

	public void removeSolution(int i) {
		solutions.remove(i);
		notifySolutionsObservers();
	}
}

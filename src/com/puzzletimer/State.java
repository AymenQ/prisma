package com.puzzletimer;

import java.util.ArrayList;
import java.util.Observable;

import com.puzzletimer.scrambles.Scrambler;
import com.puzzletimer.timer.Timer;

public class State extends Observable {
	private Scrambler scrambler;
	private ArrayList<Solution> solutions;
	private Solution currentSolution;

	public State(Scrambler scrambler) {
		this.scrambler = scrambler;
		this.solutions = new ArrayList<Solution>();
		this.currentSolution = new Solution(scrambler.getNextScramble(), new Timer());
		setChanged();
	}

	public ArrayList<Solution> getSolutions() {
		return solutions;
	}
	
	public Solution getCurrentSolution() {
		return currentSolution;
	}
	
	public void saveCurrentSolution() {
		solutions.add(currentSolution);
		currentSolution = new Solution(scrambler.getNextScramble(), new Timer());
		setChanged();
		notifyObservers();
	}

	public void removeSolution(int i) {
		solutions.remove(i);
		setChanged();
		notifyObservers();
	}
}

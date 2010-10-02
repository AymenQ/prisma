package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.models.FullSolution;

public class SolutionManager {
    private ArrayList<SolutionListener> listeners;
    private ArrayList<FullSolution> solutions;

    public SolutionManager() {
        this.listeners = new ArrayList<SolutionListener>();
        this.solutions = new ArrayList<FullSolution>();
    }

    public void loadSolutions(FullSolution[] solutions) {
        this.solutions = new ArrayList<FullSolution>();
        for (FullSolution solution : solutions) {
            this.solutions.add(solution);
        }

        notifyListeners();
    }

    public void addSolution(FullSolution solution) {
        this.solutions.add(solution);

        for (SolutionListener listener : this.listeners) {
            listener.solutionAdded(solution);
        }

        notifyListeners();
    }

    public void removeSolution(FullSolution solution) {
        this.solutions.remove(solution);

        for (SolutionListener listener : this.listeners) {
            listener.solutionRemoved(solution);
        }

        notifyListeners();
    }

    public void updateSolution(FullSolution solution) {
        for (SolutionListener listener : this.listeners) {
            listener.solutionUpdated(solution);
        }

        notifyListeners();
    }

    public void notifyListeners() {
        FullSolution[] solutions = new FullSolution[this.solutions.size()];
        for (int i = 0; i < this.solutions.size(); i++) {
            solutions[i] = this.solutions.get(i);
        }

        for (SolutionListener listener : this.listeners) {
            listener.solutionsUpdated(solutions);
        }
    }

    public void addSolutionListener(SolutionListener listener) {
        this.listeners.add(listener);
    }

    public void removeSolutionListener(SolutionListener listener) {
        this.listeners.remove(listener);
    }
}

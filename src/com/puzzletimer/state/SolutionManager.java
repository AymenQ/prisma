package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class SolutionManager {
    private ArrayList<SolutionListener> listeners;
    private ArrayList<Solution> solutions;

    public SolutionManager() {
        this.listeners = new ArrayList<SolutionListener>();
        this.solutions = new ArrayList<Solution>();
    }

    public void addSolution(Solution solution) {
        this.solutions.add(solution);

        for (SolutionListener listener : this.listeners) {
            listener.solutionAdded(solution);
        }

        notifyListeners();
    }

    public void removeSolution(Solution solution) {
        this.solutions.remove(solution);

        for (SolutionListener listener : this.listeners) {
            listener.solutionRemoved(solution);
        }

        notifyListeners();
    }

    public void notifyListeners() {
        Solution[] solutions = new Solution[this.solutions.size()];
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

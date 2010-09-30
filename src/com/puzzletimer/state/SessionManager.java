package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.models.Solution;

public class SessionManager {
    private ArrayList<SessionListener> listeners;
    private ArrayList<Solution> solutions;

    public SessionManager() {
        this.listeners = new ArrayList<SessionListener>();
        this.solutions = new ArrayList<Solution>();
    }

    public void addSolution(Solution solution) {
        this.solutions.add(solution);
        notifyListeners();
    }

    public void removeSolution(Solution solution) {
        this.solutions.remove(solution);
        notifyListeners();
    }

    public void clearSession() {
        this.solutions = new ArrayList<Solution>();
        notifyListeners();
    }

    public void notifyListeners() {
        Solution[] solutions = new Solution[this.solutions.size()];
        for (int i = 0; i < this.solutions.size(); i++) {
            solutions[i] = this.solutions.get(i);
        }

        for (SessionListener listener : this.listeners) {
            listener.solutionsUpdated(solutions);
        }
    }

    public void addSessionListener(SessionListener listener) {
        this.listeners.add(listener);
    }

    public void removeSessionListener(SessionListener listener) {
        this.listeners.remove(listener);
    }
}

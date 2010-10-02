package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.models.FullSolution;

public class SessionManager {
    private ArrayList<SessionListener> listeners;
    private ArrayList<FullSolution> solutions;

    public SessionManager() {
        this.listeners = new ArrayList<SessionListener>();
        this.solutions = new ArrayList<FullSolution>();
    }

    public void addSolution(FullSolution solution) {
        this.solutions.add(solution);
        notifyListeners();
    }

    public void removeSolution(FullSolution solution) {
        this.solutions.remove(solution);
        notifyListeners();
    }

    public void clearSession() {
        this.solutions = new ArrayList<FullSolution>();
        notifyListeners();
    }

    public void notifyListeners() {
        FullSolution[] solutions = new FullSolution[this.solutions.size()];
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

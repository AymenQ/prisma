package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.puzzletimer.models.FullSolution;

public class SessionManager {
    private ArrayList<SessionListener> listeners;
    private SortedSet<FullSolution> solutions;

    public SessionManager() {
        this.listeners = new ArrayList<SessionListener>();
        this.solutions = new TreeSet<FullSolution>(new Comparator<FullSolution>() {
            @Override
            public int compare(FullSolution solution1, FullSolution solution2) {
                Date start1 = solution1.getSolution().timing.getStart();
                Date start2 = solution2.getSolution().timing.getStart();
                return start2.compareTo(start1);
            }
        });
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
        this.solutions.clear();
        notifyListeners();
    }

    public void notifyListeners() {
        FullSolution[] solutions = new FullSolution[this.solutions.size()];
        this.solutions.toArray(solutions);

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

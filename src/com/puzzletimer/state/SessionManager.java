package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.puzzletimer.models.Solution;

public class SessionManager {
    private ArrayList<SessionListener> listeners;
    private SortedSet<Solution> solutions;

    public SessionManager() {
        this.listeners = new ArrayList<SessionListener>();
        this.solutions = new TreeSet<Solution>(new Comparator<Solution>() {
            @Override
            public int compare(Solution solution1, Solution solution2) {
                Date start1 = solution1.timing.getStart();
                Date start2 = solution2.timing.getStart();
                return start2.compareTo(start1);
            }
        });
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
        this.solutions.clear();
        notifyListeners();
    }

    public void notifyListeners() {
        Solution[] solutions = new Solution[this.solutions.size()];
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

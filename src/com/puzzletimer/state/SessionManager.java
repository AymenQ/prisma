package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.puzzletimer.models.Solution;

public class SessionManager {
    public static class Listener {
        public void solutionsUpdated(Solution[] solutions) { }
    }

    private ArrayList<Listener> listeners;
    private HashMap<UUID, Solution> solutions;

    public SessionManager() {
        this.listeners = new ArrayList<Listener>();
        this.solutions = new HashMap<UUID, Solution>();
    }

    public Solution[] getSolutions() {
        ArrayList<Solution> solutions =
            new ArrayList<Solution>(this.solutions.values());

        Collections.sort(solutions,new Comparator<Solution>() {
            @Override
            public int compare(Solution solution1, Solution solution2) {
                Date start1 = solution1.getTiming().getStart();
                Date start2 = solution2.getTiming().getStart();
                return start2.compareTo(start1);
            }
        });

        Solution[] solutionsArray = new Solution[solutions.size()];
        solutions.toArray(solutionsArray);

        return solutionsArray;
    }

    public void addSolution(Solution solution) {
        this.solutions.put(solution.getSolutionId(), solution);
        notifyListeners();
    }

    public void updateSolution(Solution solution) {
        if (this.solutions.containsKey(solution.getSolutionId())) {
            this.solutions.put(solution.getSolutionId(), solution);
            notifyListeners();
        }
    }

    public void removeSolution(Solution solution) {
        this.solutions.remove(solution.getSolutionId());
        notifyListeners();
    }

    public void clearSession() {
        this.solutions.clear();
        notifyListeners();
    }

    public void notifyListeners() {
        ArrayList<Solution> solutions =
            new ArrayList<Solution>(this.solutions.values());

        Collections.sort(solutions,new Comparator<Solution>() {
            @Override
            public int compare(Solution solution1, Solution solution2) {
                Date start1 = solution1.getTiming().getStart();
                Date start2 = solution2.getTiming().getStart();
                return start2.compareTo(start1);
            }
        });

        Solution[] solutionsArray = new Solution[solutions.size()];
        solutions.toArray(solutionsArray);

        for (Listener listener : this.listeners) {
            listener.solutionsUpdated(solutionsArray);
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}

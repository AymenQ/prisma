package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.puzzletimer.models.Solution;

public class SolutionManager {
    public static class Listener {
        public void solutionAdded(Solution solution) { }
        public void solutionsAdded(Solution[] solutions) { }
        public void solutionRemoved(Solution solution) { }
        public void solutionUpdated(Solution solution) { }
        public void solutionsUpdated(Solution[] solutions) { }
    }

    private ArrayList<Listener> listeners;
    private HashMap<UUID, Solution> solutions;

    public SolutionManager() {
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

    public void loadSolutions(Solution[] solutions) {
        this.solutions.clear();
        for (Solution solution : solutions) {
            this.solutions.put(solution.getSolutionId(), solution);
        }

        notifyListeners();
    }

    public void addSolution(Solution solution) {
        this.solutions.put(solution.getSolutionId(), solution);

        for (Listener listener : this.listeners) {
            listener.solutionAdded(solution);
        }

        notifyListeners();
    }

    public void addSolutions(Solution[] solutions) {
        for (Solution solution : solutions) {
            this.solutions.put(solution.getSolutionId(), solution);
        }

        for (Listener listener : this.listeners) {
            listener.solutionsAdded(solutions);
        }

        notifyListeners();
    }

    public void removeSolution(Solution solution) {
        this.solutions.remove(solution.getSolutionId());

        for (Listener listener : this.listeners) {
            listener.solutionRemoved(solution);
        }

        notifyListeners();
    }

    public void updateSolution(Solution solution) {
        this.solutions.put(solution.getSolutionId(), solution);

        for (Listener listener : this.listeners) {
            listener.solutionUpdated(solution);
        }

        notifyListeners();
    }

    public void notifyListeners() {
        Solution[] solutions = getSolutions();

        for (Listener listener : this.listeners) {
            listener.solutionsUpdated(solutions);
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}

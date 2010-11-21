package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.puzzletimer.models.Solution;

public class SolutionManager {
    private ArrayList<SolutionListener> listeners;
    private SortedSet<Solution> solutions;

    public SolutionManager() {
        this.listeners = new ArrayList<SolutionListener>();
        this.solutions = new TreeSet<Solution>(new Comparator<Solution>() {
            @Override
            public int compare(Solution solution1, Solution solution2) {
                Date start1 = solution1.timing.getStart();
                Date start2 = solution2.timing.getStart();
                return start2.compareTo(start1);
            }
        });
    }

    public Solution[] getSolutions() {
        return this.solutions.toArray(new Solution[this.solutions.size()]);
    }

    public void loadSolutions(Solution[] solutions) {
        this.solutions.clear();
        this.solutions.addAll(Arrays.asList(solutions));

        notifyListeners();
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

    public void updateSolution(Solution solution) {
        for (SolutionListener listener : this.listeners) {
            listener.solutionUpdated(solution);
        }

        notifyListeners();
    }

    public void notifyListeners() {
        Solution[] solutions = new Solution[this.solutions.size()];
        this.solutions.toArray(solutions);

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

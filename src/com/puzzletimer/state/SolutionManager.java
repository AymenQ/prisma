package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.puzzletimer.models.FullSolution;

public class SolutionManager {
    private ArrayList<SolutionListener> listeners;
    private SortedSet<FullSolution> solutions;

    public SolutionManager() {
        this.listeners = new ArrayList<SolutionListener>();
        this.solutions = new TreeSet<FullSolution>(new Comparator<FullSolution>() {
            @Override
            public int compare(FullSolution solution1, FullSolution solution2) {
                Date start1 = solution1.getSolution().timing.getStart();
                Date start2 = solution2.getSolution().timing.getStart();
                return start2.compareTo(start1);
            }
        });
    }

    public FullSolution[] getSolutions() {
        return this.solutions.toArray(new FullSolution[this.solutions.size()]);
    }

    public void loadSolutions(FullSolution[] solutions) {
        this.solutions.clear();
        this.solutions.addAll(Arrays.asList(solutions));

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

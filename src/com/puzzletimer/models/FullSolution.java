package com.puzzletimer.models;

public class FullSolution {
    private Solution solution;
    private Scramble scramble;

    public FullSolution(Solution solution, Scramble scramble) {
        this.solution = solution;
        this.scramble = scramble;
    }

    public Solution getSolution() {
        return this.solution;
    }

    public Scramble getScramble() {
        return this.scramble;
    }
}

package com.puzzletimer.scramblers;

import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.RubiksPocketCubeSolver;

public class RubiksPocketCubeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private RubiksPocketCubeSolver solver;
    private Random random;

    public RubiksPocketCubeRandomScrambler(ScramblerInfo scramblerInfo, int minScrambleLength, String[] generatingSet) {
        this.scramblerInfo = scramblerInfo;
        this.solver = new RubiksPocketCubeSolver(minScrambleLength, generatingSet);
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        return new Scramble(
            getScramblerInfo().getScramblerId(),
            this.solver.generate(
                this.solver.getRandomState(this.random)));
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

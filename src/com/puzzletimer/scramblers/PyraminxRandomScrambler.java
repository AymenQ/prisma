package com.puzzletimer.scramblers;

import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.PyraminxSolver;

public class PyraminxRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private PyraminxSolver solver;
    private Random random;

    public PyraminxRandomScrambler(ScramblerInfo scramblerInfo, int minScrambleLength) {
        this.scramblerInfo = scramblerInfo;
        this.solver = new PyraminxSolver(minScrambleLength);
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

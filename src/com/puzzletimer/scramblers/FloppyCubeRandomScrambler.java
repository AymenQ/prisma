package com.puzzletimer.scramblers;

import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.FloppyCubeSolver;

public class FloppyCubeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private Random random;

    public FloppyCubeRandomScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
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
            FloppyCubeSolver.generate(
                FloppyCubeSolver.getRandomState(this.random)));
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

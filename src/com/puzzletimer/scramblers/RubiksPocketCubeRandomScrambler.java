package com.puzzletimer.scramblers;

import java.util.Random;

import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.RubiksPocketCubeSolver;

public class RubiksPocketCubeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private Random random;

    public RubiksPocketCubeRandomScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public String[] getNextScrambleSequence() {
        int permutation = this.random.nextInt(RubiksPocketCubeSolver.N_PERMUTATIONS);
        int orientation = this.random.nextInt(RubiksPocketCubeSolver.N_ORIENTATIONS);
        return RubiksPocketCubeSolver.generate(permutation, orientation);
    }
}

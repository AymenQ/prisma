package com.puzzletimer.scramblers;

import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksPocketCubeSolver;

public class RubiksPocketCubeRandomScrambler implements Scrambler {
    private Random random;

    public RubiksPocketCubeRandomScrambler() {
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "2x2x2-CUBE-RANDOM";
    }

    @Override
    public String getPuzzleId() {
        return "2x2x2-CUBE";
    }

    @Override
    public String getDescription() {
        return "Random scrambler";
    }

    @Override
    public Scramble getNextScramble() {
        int permutation = this.random.nextInt(RubiksPocketCubeSolver.N_PERMUTATIONS);
        int orientation = this.random.nextInt(RubiksPocketCubeSolver.N_ORIENTATIONS);
        return new Scramble(UUID.randomUUID(), null, RubiksPocketCubeSolver.generate(permutation, orientation));
    }
}

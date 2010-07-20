package com.puzzletimer.scramblers;

import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.PyraminxSolver;

public class PyraminxRandomScrambler implements Scrambler {
    private Random random;

    public PyraminxRandomScrambler() {
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "PYRAMINX-RANDOM";
    }

    @Override
    public String getPuzzleId() {
        return "PYRAMINX";
    }

    @Override
    public String getDescription() {
        return "Random scrambler";
    }

    @Override
    public Scramble getNextScramble() {
        int tipsOrientation = this.random.nextInt(PyraminxSolver.N_VERTICES_ORIERNTATIONS);
        int verticesOrientation = this.random.nextInt(PyraminxSolver.N_VERTICES_ORIERNTATIONS);
        int edgesPermutation = this.random.nextInt(PyraminxSolver.N_EDGES_PERMUTATIONS);
        int edgesOrientation = this.random.nextInt(PyraminxSolver.N_EDGES_ORIENTATIONS);
        return new Scramble(UUID.randomUUID(), null, PyraminxSolver.generate(tipsOrientation, verticesOrientation, edgesPermutation, edgesOrientation));
    }
}

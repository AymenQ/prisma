package com.puzzletimer.scramblers;

import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.PyraminxSolver;

public class PyraminxRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private Random random;

    public PyraminxRandomScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble(UUID scrambleId, UUID categoryId) {
        int tipsOrientation = this.random.nextInt(PyraminxSolver.N_VERTICES_ORIERNTATIONS);
        int verticesOrientation = this.random.nextInt(PyraminxSolver.N_VERTICES_ORIERNTATIONS);
        int edgesPermutation = this.random.nextInt(PyraminxSolver.N_EDGES_PERMUTATIONS);
        int edgesOrientation = this.random.nextInt(PyraminxSolver.N_EDGES_ORIENTATIONS);
        return new Scramble(scrambleId, categoryId, PyraminxSolver.generate(tipsOrientation, verticesOrientation, edgesPermutation, edgesOrientation));
    }
}

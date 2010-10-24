package com.puzzletimer.scramblers;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.IndexMapping;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private boolean solvedCornersPermutation;
    private boolean solvedCornersOrientation;
    private boolean solvedEdgesPermutation;
    private boolean solvedEdgesOrientation;
    private Random random;

    public RubiksCubeRandomScrambler(
        ScramblerInfo scramblerInfo,
        boolean solvedCornersPermutation, boolean solvedCornersOrientation,
        boolean solvedEdgesPermutation, boolean solvedEdgesOrientation) {
        this.scramblerInfo = scramblerInfo;
        this.solvedCornersPermutation = solvedCornersPermutation;
        this.solvedCornersOrientation = solvedCornersOrientation;
        this.solvedEdgesPermutation = solvedEdgesPermutation;
        this.solvedEdgesOrientation = solvedEdgesOrientation;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    private int permutationSign(byte[] permutation) {
        int nInversions = 0;
        for (int i = 0; i < permutation.length; i++) {
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[i] > permutation[j]) {
                    nInversions++;
                }
            }
        }

        return nInversions % 2 == 0 ? 1 : -1;
    }

    @Override
    public Scramble getNextScramble() {
        byte[] cornersPermutation = IndexMapping.indexToPermutation(
            this.solvedCornersPermutation ? 0 : this.random.nextInt(RubiksCubeSolver.N_CORNERS_PERMUTATIONS), 8);
        byte[] cornersOrientation = IndexMapping.indexToZeroSumOrientation(
            this.solvedCornersOrientation ? 0 : this.random.nextInt(RubiksCubeSolver.N_CORNERS_ORIENTATIONS), 3, 8);
        byte[] edgesPermutation = IndexMapping.indexToPermutation(
            this.solvedEdgesPermutation ? 0 : this.random.nextInt(RubiksCubeSolver.N_EDGES_PERMUTATIONS), 12);
        byte[] edgesOrientation = IndexMapping.indexToZeroSumOrientation(
            this.solvedEdgesOrientation ? 0 : this.random.nextInt(RubiksCubeSolver.N_EDGES_ORIENTATIONS), 2, 12);

        // fix permutations parity
        if (permutationSign(cornersPermutation) != permutationSign(edgesPermutation)) {
            if (this.solvedCornersPermutation) {
                byte temp = edgesPermutation[0];
                edgesPermutation[0] = edgesPermutation[1];
                edgesPermutation[1] = temp;
            } else {
                byte temp = cornersPermutation[0];
                cornersPermutation[0] = cornersPermutation[1];
                cornersPermutation[1] = temp;
            }
        }

        RubiksCubeSolver.State state = new RubiksCubeSolver.State(
            cornersPermutation,
            cornersOrientation,
            edgesPermutation,
            edgesOrientation);

        return new Scramble(
            UUID.randomUUID(),
            getScramblerInfo().getScramblerId(),
            RubiksCubeSolver.generate(state));

    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

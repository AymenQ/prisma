package com.puzzletimer.scramblers;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.IndexMapping;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeRandomScrambler implements Scrambler {
    private Random random;

    public RubiksCubeRandomScrambler() {
        this.random = new Random();
    }

    @Override
    public String getScramblerId() {
        return "RUBIKS-CUBE-RANDOM";
    }

    @Override
    public String getPuzzleId() {
         return "RUBIKS-CUBE";
    }

    @Override
    public String getDescription() {
        return "Random scrambler";
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
            this.random.nextInt(RubiksCubeSolver.N_CORNERS_PERMUTATIONS), 8);
        byte[] cornersOrientation = IndexMapping.indexToZeroSumOrientation(
            this.random.nextInt(RubiksCubeSolver.N_CORNERS_ORIENTATIONS), 3, 8);
        byte[] edgesPermutation = IndexMapping.indexToPermutation(
            this.random.nextInt(RubiksCubeSolver.N_EDGES_PERMUTATIONS), 12);
        byte[] edgesOrientation = IndexMapping.indexToZeroSumOrientation(
            this.random.nextInt(RubiksCubeSolver.N_EDGES_ORIENTATIONS), 2, 12);

        // fix permutations parity
        if (permutationSign(cornersPermutation) != permutationSign(edgesPermutation)) {
            byte temp = cornersPermutation[0];
            cornersPermutation[0] = cornersPermutation[1];
            cornersPermutation[1] = temp;
        }

        RubiksCubeSolver.State state = new RubiksCubeSolver.State(
            cornersPermutation,
            cornersOrientation,
            edgesPermutation,
            edgesOrientation);

        return new Scramble(UUID.randomUUID(), null, RubiksCubeSolver.generate(state));
    }
}

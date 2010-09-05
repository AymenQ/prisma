package com.puzzletimer.scramblers;

import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.IndexMapping;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeLastLayerScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private boolean solvedOrientation;
    private Random random;

    public RubiksCubeLastLayerScrambler(ScramblerInfo scramblerInfo, boolean solvedOrientation) {
        this.scramblerInfo = scramblerInfo;
        this.solvedOrientation = solvedOrientation;
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
    public Scramble getNextScramble(UUID scrambleId, UUID categoryId) {
        // id
        byte[] cornersPermutation = IndexMapping.indexToPermutation(0, 8);
        byte[] cornersOrientation = IndexMapping.indexToZeroSumOrientation(0, 3, 8);
        byte[] edgesPermutation = IndexMapping.indexToPermutation(0, 12);
        byte[] edgesOrientation = IndexMapping.indexToZeroSumOrientation(0, 2, 12);

        // last layer
        byte[] lastLayerCornersPermutation = IndexMapping.indexToPermutation(
            this.random.nextInt(24), 4);
        byte[] lastLayerCornersOrientation = IndexMapping.indexToZeroSumOrientation(
            this.solvedOrientation ? 0 : this.random.nextInt(27), 3, 4);
        byte[] lastLayerEdgesPermutation = IndexMapping.indexToPermutation(
            this.random.nextInt(24), 4);
        byte[] lastLayerEdgesOrientation = IndexMapping.indexToZeroSumOrientation(
            this.solvedOrientation ? 0 : this.random.nextInt(8), 2, 4);

        for (int i = 0; i < 4; i++) {
            cornersPermutation[i + 4] = (byte) (lastLayerCornersPermutation[i] + 4);
            cornersOrientation[i + 4] = lastLayerCornersOrientation[i];
            edgesPermutation[i + 8] = (byte) (lastLayerEdgesPermutation[i] + 8);
            edgesOrientation[i + 8] = lastLayerEdgesOrientation[i];
        }

        // fix permutations parity
        if (permutationSign(cornersPermutation) != permutationSign(edgesPermutation)) {
            byte temp = cornersPermutation[4];
            cornersPermutation[4] = cornersPermutation[5];
            cornersPermutation[5] = temp;
        }

        RubiksCubeSolver.State state = new RubiksCubeSolver.State(
            cornersPermutation,
            cornersOrientation,
            edgesPermutation,
            edgesOrientation);

        return new Scramble(scrambleId, categoryId, RubiksCubeSolver.generate(state));
    }
}

package com.puzzletimer.scramblers;
import java.util.Random;
import java.util.UUID;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.IndexMapping;
import com.puzzletimer.solvers.RubiksCubeCrossSolver;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeControlledCrossDistanceScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private int minDistance;
    private int maxDistance;
    private Random random;

    public RubiksCubeControlledCrossDistanceScrambler(ScramblerInfo scramblerInfo, int minDistance, int maxDistance) {
        this.scramblerInfo = scramblerInfo;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    private int edgesOrientation(byte[] orientation) {
        int sum = 0;
        for (int i = 0; i < orientation.length; i++) {
            sum += orientation[i];
        }

        return sum % 2;
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
        // random corners
        byte[] cornersPermutation = IndexMapping.indexToPermutation(
            this.random.nextInt(RubiksCubeSolver.N_CORNERS_PERMUTATIONS), 8);
        byte[] cornersOrientation = IndexMapping.indexToZeroSumOrientation(
            this.random.nextInt(RubiksCubeSolver.N_CORNERS_ORIENTATIONS), 3, 8);

        // search for a cross state whose distance is between minDistance and maxDistance
        int combinationIndex;
        int permutationIndex;
        int orientationIndex;
        do {
            combinationIndex = this.random.nextInt(RubiksCubeCrossSolver.N_COMBINATIONS);
            permutationIndex = this.random.nextInt(RubiksCubeCrossSolver.N_PERMUTATIONS);
            orientationIndex = this.random.nextInt(RubiksCubeCrossSolver.N_ORIENTATIONS);
        } while (
            RubiksCubeCrossSolver.distance[combinationIndex][permutationIndex][orientationIndex] < this.minDistance ||
            RubiksCubeCrossSolver.distance[combinationIndex][permutationIndex][orientationIndex] > this.maxDistance);

        // cross edges state
        boolean[] crossCombination = IndexMapping.indexToCombination(combinationIndex, 4, 12);
        byte[] crossPermutation = IndexMapping.indexToPermutation(permutationIndex, 4);
        byte[] crossOrientation = IndexMapping.indexToOrientation(orientationIndex, 2, 4);

        // remaining edges state
        byte[] permutation = IndexMapping.indexToPermutation(this.random.nextInt(40320), 8);
        byte[] orientation = IndexMapping.indexToOrientation(this.random.nextInt(256), 2, 8);

        // fix edges orientation
        if (edgesOrientation(crossOrientation) != edgesOrientation(orientation)) {
            orientation[0] = (byte) (orientation[0] == 0 ? 1 : 0);
        }

        // mapping between edge positions on cross solver and cube solver
        int[] crossToCube = { 4, 5, 6, 7, 0, 1, 2, 3, 8, 9, 10, 11 };

        // merge cross edges and remaining edges
        byte[] edgesPermutation = new byte[12];
        byte[] edgesOrientation = new byte[12];

        int crossNext = 0;
        int next = 0;
        for (int i = 0; i < crossCombination.length; i++) {
            if (crossCombination[i]) {
                edgesPermutation[crossToCube[i]] = (byte) crossToCube[crossPermutation[crossNext]];
                edgesOrientation[crossToCube[i]] = crossOrientation[crossPermutation[crossNext]];
                crossNext++;
            } else {
                edgesPermutation[crossToCube[i]] = (byte) crossToCube[permutation[next] + 4];
                edgesOrientation[crossToCube[i]] = orientation[next];
                next++;
            }
        }

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

        return new Scramble(scrambleId, categoryId, RubiksCubeSolver.generate(state));
    }
}

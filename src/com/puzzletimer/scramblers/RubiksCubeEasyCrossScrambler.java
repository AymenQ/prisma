package com.puzzletimer.scramblers;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.IndexMapping;
import com.puzzletimer.solvers.RubiksCubeCrossSolver;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeEasyCrossScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private int maxDistance;
    private Random random;

    public RubiksCubeEasyCrossScrambler(ScramblerInfo scramblerInfo, int maxDistance) {
        this.scramblerInfo = scramblerInfo;
        this.maxDistance = maxDistance;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        RubiksCubeCrossSolver.State crossState;
        for (;;) {
            int combination = this.random.nextInt(RubiksCubeCrossSolver.N_COMBINATIONS);
            int permutation = this.random.nextInt(RubiksCubeCrossSolver.N_PERMUTATIONS);
            int orientation = this.random.nextInt(RubiksCubeCrossSolver.N_ORIENTATIONS);

            if (RubiksCubeCrossSolver.distance[combination][permutation][orientation] <= this.maxDistance) {
                crossState =
                    new RubiksCubeCrossSolver.State(
                        IndexMapping.indexToCombination(combination, 4, 12),
                        IndexMapping.indexToPermutation(permutation, 4),
                        IndexMapping.indexToOrientation(orientation, 2, 4));
                break;
            }
        }

        RubiksCubeSolver.State cubeState =
            new RubiksCubeSolver.State(
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1,  4,  5,  6,  7, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1,  0,  0,  0,  0, -1, -1, -1, -1 });
        cubeState = cubeState.applySequence(RubiksCubeCrossSolver.generate(crossState));

        RubiksCubeRandomScrambler scrambler =
            new RubiksCubeRandomScrambler(
                getScramblerInfo(),
                cubeState.cornersPermutation,
                cubeState.cornersOrientation,
                cubeState.edgesPermutation,
                cubeState.edgesOrientation);

        return scrambler.getNextScramble();
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

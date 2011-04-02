package com.puzzletimer.scramblers;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeSingleStickerCycleScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private RubiksCubeRandomScrambler rubiksCubeRandomScrambler;

    public RubiksCubeSingleStickerCycleScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
        this.rubiksCubeRandomScrambler = new RubiksCubeRandomScrambler(
            new ScramblerInfo("RUBIKS-CUBE-RANDOM", "RUBIKS-CUBE", "Random scrambler"),
            new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
            new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
            new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        RubiksCubeSolver.State state = null;

        boolean singleCornerCycle = false;
        boolean singleEdgeCycle = false;
        while (!singleCornerCycle || !singleEdgeCycle) {
            state = this.rubiksCubeRandomScrambler.getRandomState();

            // corners
            boolean[] solvedCorners = new boolean[8];
            for (int i = 0; i < solvedCorners.length; i++) {
                solvedCorners[i] =
                    state.cornersPermutation[i] == i &&
                    state.cornersOrientation[i] == 0;
            }
            solvedCorners[0] = true;

            int currentCorner = 0;
            for (;;) {
                int nextCorner = state.cornersPermutation[currentCorner];

                // end of cycle
                if (nextCorner == 0) {
                    boolean solved = true;
                    for (int i = 0; i < solvedCorners.length; i++) {
                        if (!solvedCorners[i]) {
                            solved = false;
                        }
                    }

                    singleCornerCycle = solved;
                    break;
                }

                currentCorner = nextCorner;
                solvedCorners[currentCorner] = true;
            }

            // edges
            boolean[] solvedEdges = new boolean[12];
            for (int i = 0; i < solvedEdges.length; i++) {
                solvedEdges[i] =
                    state.edgesPermutation[i] == i &&
                    state.edgesOrientation[i] == 0;
            }
            solvedEdges[0] = true;

            int currentEdge = 0;
            for (;;) {
                int nextEdge = state.edgesPermutation[currentEdge];

                // end of cycle
                if (nextEdge == 0) {
                    boolean solved = true;
                    for (int i = 0; i < solvedEdges.length; i++) {
                        if (!solvedEdges[i]) {
                            solved = false;
                        }
                    }

                    singleEdgeCycle = solved;
                    break;
                }

                currentEdge = nextEdge;
                solvedEdges[currentEdge] = true;
            }
        }

        return new Scramble(
            getScramblerInfo().getScramblerId(),
            RubiksCubeSolver.generate(state));
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

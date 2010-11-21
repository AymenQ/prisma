package com.puzzletimer.scramblers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeRandomScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private byte[] cornersPermutation;
    private byte[] cornersOrientation;
    private byte[] edgesPermutation;
    private byte[] edgesOrientation;
    private Random random;

    public RubiksCubeRandomScrambler(
            ScramblerInfo scramblerInfo,
            byte[] cornersPermutation,
            byte[] cornersOrientation,
            byte[] edgesPermutation,
            byte[] edgesOrientation) {
        this.scramblerInfo = scramblerInfo;
        this.cornersPermutation = cornersPermutation;
        this.cornersOrientation = cornersOrientation;
        this.edgesPermutation = edgesPermutation;
        this.edgesOrientation = edgesOrientation;
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
        byte[] cornersPermutation;
        byte[] cornersOrientation;
        byte[] edgesPermutation;
        byte[] edgesOrientation;

        do {
            // corners permutation
            ArrayList<Byte> undefinedCornersPermutation = new ArrayList<Byte>();
            for (int i = 0; i < this.cornersPermutation.length; i++) {
                undefinedCornersPermutation.add((byte) i);
            }

            for (int i = 0; i < this.cornersPermutation.length; i++) {
                if (this.cornersPermutation[i] >= 0) {
                    undefinedCornersPermutation.remove((Byte) this.cornersPermutation[i]);
                }
            }

            Collections.shuffle(undefinedCornersPermutation, this.random);

            cornersPermutation = new byte[this.cornersPermutation.length];
            for (int i = 0; i < cornersPermutation.length; i++) {
                if (this.cornersPermutation[i] >= 0) {
                    cornersPermutation[i] = this.cornersPermutation[i];
                } else {
                    cornersPermutation[i] = undefinedCornersPermutation.get(0);
                    undefinedCornersPermutation.remove(0);
                }
            }

            // corners orientation
            int nUndefinedCornerOrientations = 0;
            for (int i = 0; i < this.cornersOrientation.length; i++) {
                if (this.cornersOrientation[i] < 0) {
                    nUndefinedCornerOrientations++;
                }
            }

            int cornersOrientationSum = 0;
            cornersOrientation = new byte[this.cornersOrientation.length];
            for (int i = 0; i < cornersOrientation.length; i++) {
                if (this.cornersOrientation[i] >= 0) {
                    cornersOrientation[i] = this.cornersOrientation[i];
                } else {
                    if (nUndefinedCornerOrientations == 1) {
                        cornersOrientation[i] = (byte) ((3 - cornersOrientationSum) % 3);
                    } else {
                        cornersOrientation[i] = (byte) this.random.nextInt(3);
                    }

                    nUndefinedCornerOrientations--;
                }

                cornersOrientationSum += cornersOrientation[i];
                cornersOrientationSum %= 3;
            }

            // edges permutation
            ArrayList<Byte> undefinedEdgesPermutation = new ArrayList<Byte>();
            for (int i = 0; i < this.edgesPermutation.length; i++) {
                undefinedEdgesPermutation.add((byte) i);
            }

            for (int i = 0; i < this.edgesPermutation.length; i++) {
                if (this.edgesPermutation[i] >= 0) {
                    undefinedEdgesPermutation.remove((Byte) this.edgesPermutation[i]);
                }
            }

            Collections.shuffle(undefinedEdgesPermutation, this.random);

            edgesPermutation = new byte[this.edgesPermutation.length];
            for (int i = 0; i < edgesPermutation.length; i++) {
                if (this.edgesPermutation[i] >= 0) {
                    edgesPermutation[i] = this.edgesPermutation[i];
                } else {
                    edgesPermutation[i] = undefinedEdgesPermutation.get(0);
                    undefinedEdgesPermutation.remove(0);
                }
            }

            // edges orientation
            int nUndefinedEdgeOrientations = 0;
            for (int i = 0; i < this.edgesOrientation.length; i++) {
                if (this.edgesOrientation[i] < 0) {
                    nUndefinedEdgeOrientations++;
                }
            }

            int edgesOrientationSum = 0;
            edgesOrientation = new byte[this.edgesOrientation.length];
            for (int i = 0; i < edgesOrientation.length; i++) {
                if (this.edgesOrientation[i] >= 0) {
                    edgesOrientation[i] = this.edgesOrientation[i];
                } else {
                    if (nUndefinedEdgeOrientations == 1) {
                        edgesOrientation[i] = (byte) ((2 - edgesOrientationSum) % 2);
                    } else {
                        edgesOrientation[i] = (byte) this.random.nextInt(2);
                    }

                    nUndefinedEdgeOrientations--;
                }

                edgesOrientationSum += edgesOrientation[i];
                edgesOrientationSum %= 2;
            }
        } while (permutationSign(cornersPermutation) != permutationSign(edgesPermutation));

        RubiksCubeSolver.State state = new RubiksCubeSolver.State(
            cornersPermutation,
            cornersOrientation,
            edgesPermutation,
            edgesOrientation);

        return new Scramble(
            getScramblerInfo().getScramblerId(),
            RubiksCubeSolver.generate(state));
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

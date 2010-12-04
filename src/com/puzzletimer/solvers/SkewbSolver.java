package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SkewbSolver {
    public static class State {
        public byte[] facesPermutation;
        public byte[] freeCornersPermutation;
        public byte[] freeCornersOrientation;
        public byte[] fixedCornersOrientation;

        public State(byte[] facesPermutation, byte[] freeCornersPermutation, byte[] freeCornersOrientation,
            byte[] fixedCornersOrientation) {
            this.facesPermutation = facesPermutation;
            this.freeCornersPermutation = freeCornersPermutation;
            this.freeCornersOrientation = freeCornersOrientation;
            this.fixedCornersOrientation = fixedCornersOrientation;
        }

        public State multiply(State move) {
            // faces permutation
            byte[] facesPermutation = new byte[6];
            for (int i = 0; i < facesPermutation.length; i++) {
                facesPermutation[i] =
                    this.facesPermutation[move.facesPermutation[i]];
            }

            // free corners permutation
            byte[] freeCornersPermutation = new byte[4];
            for (int i = 0; i < freeCornersPermutation.length; i++) {
                freeCornersPermutation[i] =
                    this.freeCornersPermutation[move.freeCornersPermutation[i]];
            }

            // free corners orientation
            byte[] freeCornersOrientation = new byte[4];
            for (int i = 0; i < freeCornersOrientation.length; i++) {
                freeCornersOrientation[i] =
                    (byte) ((this.freeCornersOrientation[move.freeCornersPermutation[i]] +
                        move.freeCornersOrientation[i]) % 3);
            }

            // fixed corners orientation
            byte[] fixedCornersOrientation = new byte[4];
            for (int i = 0; i < freeCornersOrientation.length; i++) {
                fixedCornersOrientation[i] =
                    (byte) ((this.fixedCornersOrientation[i] +
                        move.fixedCornersOrientation[i]) % 3);
            }

            return new State(
                facesPermutation,
                freeCornersPermutation,
                freeCornersOrientation,
                fixedCornersOrientation);
        }
    }

    private final int N_FACES_PERMUTATIONS = 360;
    private final int N_FREE_CORNERS_PERMUTATION = 12;
    private final int N_FREE_CORNERS_ORIENTATION = 27;
    private final int N_FIXED_CORNERS_ORIENTATION = 81;

    private boolean initialized;

    private State[] moves;
    private int[][] facesPermutationMove;
    private int[][] freeCornersPermutationMove;
    private int[][] freeCornersOrientationMove;
    private int[][] fixedCornersOrientationMove;
    private int[][][][] distance;

    public SkewbSolver() {
        this.initialized = false;
    }

    private void initialize() {
        // moves
        State moveL = new State(new byte[] { 1, 4, 2, 3, 0, 5 }, new byte[] { 2, 0, 1, 3 }, new byte[] { 2, 2, 2, 0 }, new byte[] { 1, 0, 0, 0 });
        State moveR = new State(new byte[] { 3, 1, 0, 2, 4, 5 }, new byte[] { 1, 3, 2, 0 }, new byte[] { 2, 2, 0, 2 }, new byte[] { 0, 1, 0, 0 });
        State moveD = new State(new byte[] { 0, 1, 2, 4, 5, 3 }, new byte[] { 0, 2, 3, 1 }, new byte[] { 0, 2, 2, 2 }, new byte[] { 0, 0, 0, 1 });
        State moveB = new State(new byte[] { 0, 2, 5, 3, 4, 1 }, new byte[] { 3, 1, 0, 2 }, new byte[] { 2, 0, 2, 2 }, new byte[] { 0, 0, 1, 0 });

        this.moves = new State[] {
            moveL,
            moveL.multiply(moveL),
            moveR,
            moveR.multiply(moveR),
            moveD,
            moveD.multiply(moveD),
            moveB,
            moveB.multiply(moveB),
        };

        // move tables
        this.facesPermutationMove = new int[this.N_FACES_PERMUTATIONS][this.moves.length];
        for (int i = 0; i < this.facesPermutationMove.length; i++) {
            State state = new State(
                IndexMapping.indexToEvenPermutation(i, 6),
                new byte[4],
                new byte[4],
                new byte[4]);
            for (int j = 0; j < this.moves.length; j++) {
                this.facesPermutationMove[i][j] =
                    IndexMapping.evenPermutationToIndex(
                        state.multiply(this.moves[j]).facesPermutation);
            }
        }

        this.freeCornersPermutationMove = new int[this.N_FREE_CORNERS_PERMUTATION][this.moves.length];
        for (int i = 0; i < this.freeCornersPermutationMove.length; i++) {
            State state = new State(
                new byte[6],
                IndexMapping.indexToEvenPermutation(i, 4),
                new byte[4],
                new byte[4]);
            for (int j = 0; j < this.moves.length; j++) {
                this.freeCornersPermutationMove[i][j] =
                    IndexMapping.evenPermutationToIndex(
                        state.multiply(this.moves[j]).freeCornersPermutation);
            }
        }

        this.freeCornersOrientationMove = new int[this.N_FREE_CORNERS_ORIENTATION][this.moves.length];
        for (int i = 0; i < this.freeCornersOrientationMove.length; i++) {
            State state = new State(
                new byte[6],
                new byte[4],
                IndexMapping.indexToZeroSumOrientation(i, 3, 4),
                new byte[4]);
            for (int j = 0; j < this.moves.length; j++) {
                this.freeCornersOrientationMove[i][j] =
                    IndexMapping.zeroSumOrientationToIndex(
                        state.multiply(this.moves[j]).freeCornersOrientation, 3);
            }
        }

        this.fixedCornersOrientationMove = new int[this.N_FIXED_CORNERS_ORIENTATION][this.moves.length];
        for (int i = 0; i < this.fixedCornersOrientationMove.length; i++) {
            State state = new State(
                new byte[6],
                new byte[4],
                new byte[4],
                IndexMapping.indexToOrientation(i, 3, 4));
            for (int j = 0; j < this.moves.length; j++) {
                this.fixedCornersOrientationMove[i][j] =
                    IndexMapping.orientationToIndex(
                        state.multiply(this.moves[j]).fixedCornersOrientation, 3);
            }
        }

        // distance table
        this.distance = new int[this.N_FACES_PERMUTATIONS]
                               [this.N_FREE_CORNERS_PERMUTATION]
                               [this.N_FREE_CORNERS_ORIENTATION]
                               [this.N_FIXED_CORNERS_ORIENTATION];
        for (int i = 0; i < this.distance.length; i++) {
            for (int j = 0; j < this.distance[i].length; j++) {
                for (int k = 0; k < this.distance[i][j].length; k++) {
                    for (int m = 0; m < this.distance[i][j][k].length; m++) {
                        this.distance[i][j][k][m] = -1;
                    }
                }
            }
        }

        this.distance[0][0][0][0] = 0;

        int nVisited;
        int depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.distance.length; i++) {
                for (int j = 0; j < this.distance[i].length; j++) {
                    for (int k = 0; k < this.distance[i][j].length; k++) {
                        for (int m = 0; m < this.distance[i][j][k].length; m++) {
                            if (this.distance[i][j][k][m] == depth) {
                                for (int moveIndex = 0; moveIndex < this.moves.length; moveIndex++) {
                                    int nextFacesPermutation = this.facesPermutationMove[i][moveIndex];
                                    int nextFreeCornersPemutation = this.freeCornersPermutationMove[j][moveIndex];
                                    int nextFreeCornersOrientation = this.freeCornersOrientationMove[k][moveIndex];
                                    int nextFixedCornersOrientation = this.fixedCornersOrientationMove[m][moveIndex];

                                    if (this.distance[nextFacesPermutation]
                                                     [nextFreeCornersPemutation]
                                                     [nextFreeCornersOrientation]
                                                     [nextFixedCornersOrientation] == -1) {
                                        this.distance[nextFacesPermutation]
                                                     [nextFreeCornersPemutation]
                                                     [nextFreeCornersOrientation]
                                                     [nextFixedCornersOrientation] = depth + 1;
                                        nVisited++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.initialized = true;
    }

    public String[] solve(State state) {
        if (!this.initialized) {
            initialize();
        }

        String[] moveNames = { "L", "L'", "R", "R'", "D", "D'", "B", "B'" };

        ArrayList<String> sequence = new ArrayList<String>();

        int facesPermutation =
            IndexMapping.evenPermutationToIndex(
                state.facesPermutation);
        int freeCornersPermutation =
            IndexMapping.evenPermutationToIndex(
                state.freeCornersPermutation);
        int freeCornersOrientation =
            IndexMapping.zeroSumOrientationToIndex(
                state.freeCornersOrientation, 3);
        int fixedCornersOrientation =
            IndexMapping.orientationToIndex(
                state.fixedCornersOrientation, 3);

        for (;;) {
            if (this.distance[facesPermutation]
                             [freeCornersPermutation]
                             [freeCornersOrientation]
                             [fixedCornersOrientation] == 0) {
                break;
            }

            for (int k = 0; k < this.moves.length; k++) {
                int nextFacesPermutation = this.facesPermutationMove[facesPermutation][k];
                int nextFreeCornersPemutation = this.freeCornersPermutationMove[freeCornersPermutation][k];
                int nextFreeCornersOrientation = this.freeCornersOrientationMove[freeCornersOrientation][k];
                int nextFixedCornersOrientation = this.fixedCornersOrientationMove[fixedCornersOrientation][k];

                if (this.distance[nextFacesPermutation]
                                 [nextFreeCornersPemutation]
                                 [nextFreeCornersOrientation]
                                 [nextFixedCornersOrientation] ==
                    this.distance[facesPermutation]
                                 [freeCornersPermutation]
                                 [freeCornersOrientation]
                                 [fixedCornersOrientation] - 1) {
                    sequence.add(moveNames[k]);
                    facesPermutation = nextFacesPermutation;
                    freeCornersPermutation = nextFreeCornersPemutation;
                    freeCornersOrientation = nextFreeCornersOrientation;
                    fixedCornersOrientation = nextFixedCornersOrientation;
                    break;
                }
            }
        }

        String[] sequenceArray = new String[sequence.size()];
        sequence.toArray(sequenceArray);

        return sequenceArray;
    }

    public String[] generate(State state) {
        String[] solution = solve(state);

        HashMap<String, String> inverseMoves = new HashMap<String, String>();
        inverseMoves.put("L",  "L'");
        inverseMoves.put("L'", "L");
        inverseMoves.put("R",  "R'");
        inverseMoves.put("R'", "R");
        inverseMoves.put("D",  "D'");
        inverseMoves.put("D'", "D");
        inverseMoves.put("B",  "B'");
        inverseMoves.put("B'", "B");

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoves.get(solution[solution.length - 1 - i]);
        }

        return sequence;
    }

    public State getRandomState(Random random) {
        if (!this.initialized) {
            initialize();
        }

        for (;;) {
            int indexFacesPermutation =
                random.nextInt(this.N_FACES_PERMUTATIONS);
            int indexFreeCornersPermutation =
                random.nextInt(this.N_FREE_CORNERS_PERMUTATION);
            int indexFreeCornersOrientation =
                random.nextInt(this.N_FREE_CORNERS_ORIENTATION);
            int indexFixedCornersOrientation =
                random.nextInt(this.N_FIXED_CORNERS_ORIENTATION);

            if (this.distance[indexFacesPermutation]
                             [indexFreeCornersPermutation]
                             [indexFreeCornersOrientation]
                             [indexFixedCornersOrientation] == -1) {
                continue;
            }

            return new State(
                IndexMapping.indexToEvenPermutation(indexFacesPermutation, 6),
                IndexMapping.indexToEvenPermutation(indexFreeCornersPermutation, 4),
                IndexMapping.indexToZeroSumOrientation(indexFreeCornersOrientation, 3, 4),
                IndexMapping.indexToOrientation(indexFixedCornersOrientation, 3, 4));
        }
    }
}

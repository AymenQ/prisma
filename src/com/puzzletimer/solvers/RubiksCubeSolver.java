// kociemba's two phase algorithm
// references: http://kociemba.org/cube.htm
//             http://www.jaapsch.net/puzzles/compcube.htm

package com.puzzletimer.solvers;

import java.util.Arrays;

public class RubiksCubeSolver {
    public static class State {
        public byte[] cornersPermutation;
        public byte[] cornersOrientation;
        public byte[] edgesPermutation;
        public byte[] edgesOrientation;

        public State(byte[] cornersPermutation, byte[] cornersOrientation, byte[] edgesPermutation, byte[] edgesOrientation) {
            this.cornersPermutation = cornersPermutation;
            this.cornersOrientation = cornersOrientation;
            this.edgesPermutation = edgesPermutation;
            this.edgesOrientation = edgesOrientation;
        }

        public State multiply(State move) {
            // corners
            byte[] cornersPermutation = new byte[8];
            byte[] cornersOrientation = new byte[8];

            for (int i = 0; i < 8; i++) {
                cornersPermutation[i] = this.cornersPermutation[move.cornersPermutation[i]];
                cornersOrientation[i] = (byte) ((this.cornersOrientation[move.cornersPermutation[i]] + move.cornersOrientation[i]) % 3);
            }

            // edges
            byte[] edgesPermutation = new byte[12];
            byte[] edgesOrientation = new byte[12];

            for (int i = 0; i < 12; i++) {
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
                edgesOrientation[i] = (byte) ((this.edgesOrientation[move.edgesPermutation[i]] + move.edgesOrientation[i]) % 2);
            }

            return new State(cornersPermutation, cornersOrientation, edgesPermutation, edgesOrientation);
        }
    }

    // constants
    public static final int N_MOVES_1 = 18;
    public static final int N_CORNERS_ORIENTATIONS = 2187;
    public static final int N_EDGES_ORIENTATIONS = 2048;
    public static final int N_E_EDGES_COMBINATIONS = 495;
    public static final int N_MOVES_2 = 10;
    public static final int N_CORNERS_PERMUTATIONS = 40320;
    public static final int N_U_D_EDGES_PERMUTATIONS = 40320;
    public static final int N_E_EDGES_PERMUTATIONS = 24;
    public static final int N_EDGES_PERMUTATIONS = 479001600;

    // moves
    private static State[] moves;
    private static int[] moves1;
    private static int[] moves2;

    static {
        State moveU = new State(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 7, 4, 5, 6, 8, 9, 10, 11 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveD = new State(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 8 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveL = new State(new byte[] { 4, 1, 2, 0, 7, 5, 6, 3 }, new byte[] { 2, 0, 0, 1, 1, 0, 0, 2 }, new byte[] { 11, 1, 2, 7, 4, 5, 6, 0, 8, 9, 10, 3 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveR = new State(new byte[] { 0, 2, 6, 3, 4, 1, 5, 7 }, new byte[] { 0, 1, 2, 0, 0, 2, 1, 0 }, new byte[] { 0, 5, 9, 3, 4, 2, 6, 7, 8, 1, 10, 11 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveF = new State(new byte[] { 0, 1, 3, 7, 4, 5, 2, 6 }, new byte[] { 0, 0, 1, 2, 0, 0, 2, 1 }, new byte[] { 0, 1, 6, 10, 4, 5, 3, 7, 8, 9, 2, 11 }, new byte[] { 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0 });
        State moveB = new State(new byte[] { 1, 5, 2, 3, 0, 4, 6, 7 }, new byte[] { 1, 2, 0, 0, 2, 1, 0, 0 }, new byte[] { 4, 8, 2, 3, 1, 5, 6, 7, 0, 9, 10, 11 }, new byte[] { 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 });

        moves = new State[] {
            moveU,
            moveU.multiply(moveU),
            moveU.multiply(moveU).multiply(moveU),
            moveD,
            moveD.multiply(moveD),
            moveD.multiply(moveD).multiply(moveD),
            moveL,
            moveL.multiply(moveL),
            moveL.multiply(moveL).multiply(moveL),
            moveR,
            moveR.multiply(moveR),
            moveR.multiply(moveR).multiply(moveR),
            moveF,
            moveF.multiply(moveF),
            moveF.multiply(moveF).multiply(moveF),
            moveB,
            moveB.multiply(moveB),
            moveB.multiply(moveB).multiply(moveB),
        };

        // phase 1
        moves1 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };

        // phase 2
        moves2 = new int[] { 0, 1, 2, 3, 4, 5, 7, 10, 13, 16 };
    }

    // move tables
    private static int[][] cornersOrientationMove;
    private static int[][] edgesOrientationMove;
    private static int[][] eEdgesCombinationMove;
    private static int[][] cornersPermutationMove;
    private static int[][] uDEdgesPermutationMove;
    private static int[][] eEdgesPermutationMove;

    static {
        // phase 1
        cornersOrientationMove = new int[N_CORNERS_ORIENTATIONS][N_MOVES_1];
        for (int i = 0; i < N_CORNERS_ORIENTATIONS; i++) {
            State state = new State(new byte[8], IndexMapping.indexToZeroSumOrientation(i, 3, 8), new byte[12], new byte[12]);
            for (int j = 0; j < N_MOVES_1; j++) {
                cornersOrientationMove[i][j] = IndexMapping.zeroSumOrientationToIndex(state.multiply(moves[moves1[j]]).cornersOrientation, 3);
            }
        }


        edgesOrientationMove = new int[N_EDGES_ORIENTATIONS][N_MOVES_1];
        for (int i = 0; i < N_EDGES_ORIENTATIONS; i++) {
            State state = new State(new byte[8], new byte[8], new byte[12], IndexMapping.indexToZeroSumOrientation(i, 2, 12));
            for (int j = 0; j < N_MOVES_1; j++) {
                edgesOrientationMove[i][j] = IndexMapping.zeroSumOrientationToIndex(state.multiply(moves[moves1[j]]).edgesOrientation, 2);
            }
        }


        eEdgesCombinationMove = new int[N_E_EDGES_COMBINATIONS][N_MOVES_1];
        for (int i = 0; i < N_E_EDGES_COMBINATIONS; i++) {
            boolean[] combination = IndexMapping.indexToCombination(i, 4, 12);

            byte[] edges = new byte[12];
            byte nextE = 0;
            byte nextUD = 4;

            for (int j = 0; j < edges.length; j++) {
                if (combination[j]) {
                    edges[j] = nextE++;
                } else {
                    edges[j] = nextUD++;
                }
            }

            State state = new State(new byte[8], new byte[8], edges, new byte[12]);
            for (int j = 0; j < N_MOVES_1; j++) {
                State result = state.multiply(moves[moves1[j]]);

                boolean[] isEEdge = new boolean[12];
                for (int k = 0; k < isEEdge.length; k++) {
                    isEEdge[k] = result.edgesPermutation[k] < 4;
                }

                eEdgesCombinationMove[i][j] = IndexMapping.combinationToIndex(isEEdge, 4);
            }
        }


        // phase 2
        cornersPermutationMove = new int[N_CORNERS_PERMUTATIONS][N_MOVES_2];
        for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
            State state = new State(IndexMapping.indexToPermutation(i, 8), new byte[8], new byte[12], new byte[12]);
            for (int j = 0; j < N_MOVES_2; j++) {
                cornersPermutationMove[i][j] = IndexMapping.permutationToIndex(state.multiply(moves[moves2[j]]).cornersPermutation);
            }
        }


        uDEdgesPermutationMove = new int[N_U_D_EDGES_PERMUTATIONS][N_MOVES_2];
        for (int i = 0; i < N_U_D_EDGES_PERMUTATIONS; i++) {
            byte[] permutation = IndexMapping.indexToPermutation(i, 8);

            byte[] edges = new byte[12];
            for (int j = 0; j < edges.length; j++) {
                edges[j] = j >= 4 ? permutation[j - 4] : (byte) j;
            }

            State state = new State(new byte[8], new byte[8], edges, new byte[12]);
            for (int j = 0; j < N_MOVES_2; j++) {
                State result = state.multiply(moves[moves2[j]]);

                byte[] uDEdges = new byte[8];
                for (int k = 0; k < uDEdges.length; k++) {
                    uDEdges[k] = (byte) (result.edgesPermutation[k + 4] - 4);
                }

                uDEdgesPermutationMove[i][j] = IndexMapping.permutationToIndex(uDEdges);
            }
        }


        eEdgesPermutationMove = new int[N_E_EDGES_PERMUTATIONS][N_MOVES_2];
        for (int i = 0; i < N_E_EDGES_PERMUTATIONS; i++) {
            byte[] permutation = IndexMapping.indexToPermutation(i, 4);

            byte[] edges = new byte[12];
            for (int j = 0; j < edges.length; j++) {
                edges[j] = j >= 4 ? (byte) j : permutation[j];
            }

            State state = new State(new byte[8], new byte[8], edges, new byte[12]);
            for (int j = 0; j < N_MOVES_2; j++) {
                State result = state.multiply(moves[moves2[j]]);

                byte[] eEdges = new byte[4];
                for (int k = 0; k < eEdges.length; k++) {
                    eEdges[k] = result.edgesPermutation[k];
                }

                eEdgesPermutationMove[i][j] = IndexMapping.permutationToIndex(eEdges);
            }
        }
    }

    // prune tables
    private static byte[][] cornersOrientationDistance;
    private static byte[][] edgesOrientationDistance;
    private static byte[][] cornersPermutationDistance;
    private static byte[][] uDEdgesPermutationDistance;

    static {
        // phase 1
        cornersOrientationDistance = new byte[N_CORNERS_ORIENTATIONS][N_E_EDGES_COMBINATIONS];
        for (int i = 0; i < cornersOrientationDistance.length; i++) {
            for (int j = 0; j < cornersOrientationDistance[i].length; j++) {
                cornersOrientationDistance[i][j] = -1;
            }
        }
        cornersOrientationDistance[0][0] = 0;

        int distance = 0;
        int nVisited = 1;
        while (nVisited < N_CORNERS_ORIENTATIONS * N_E_EDGES_COMBINATIONS) {
            for (int i = 0; i < N_CORNERS_ORIENTATIONS; i++) {
                for (int j = 0; j < N_E_EDGES_COMBINATIONS; j++) {
                    if (cornersOrientationDistance[i][j] == distance) {
                        for (int k = 0; k < N_MOVES_1; k++) {
                            int nextCornersOrientation = cornersOrientationMove[i][k];
                            int nextEEdgesCombination = eEdgesCombinationMove[j][k];
                            if (cornersOrientationDistance[nextCornersOrientation][nextEEdgesCombination] < 0) {
                                cornersOrientationDistance[nextCornersOrientation][nextEEdgesCombination] = (byte) (distance + 1);
                                nVisited++;
                            }
                        }
                    }
                }
            }
            distance++;
        }


        edgesOrientationDistance = new byte[N_EDGES_ORIENTATIONS][N_E_EDGES_COMBINATIONS];
        for (int i = 0; i < edgesOrientationDistance.length; i++) {
            for (int j = 0; j < edgesOrientationDistance[i].length; j++) {
                edgesOrientationDistance[i][j] = -1;
            }
        }
        edgesOrientationDistance[0][0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_EDGES_ORIENTATIONS * N_E_EDGES_COMBINATIONS) {
            for (int i = 0; i < N_EDGES_ORIENTATIONS; i++) {
                for (int j = 0; j < N_E_EDGES_COMBINATIONS; j++) {
                    if (edgesOrientationDistance[i][j] == distance) {
                        for (int k = 0; k < N_MOVES_1; k++) {
                            int nextEdgesOrientation = edgesOrientationMove[i][k];
                            int nextEEdgesCombination = eEdgesCombinationMove[j][k];
                            if (edgesOrientationDistance[nextEdgesOrientation][nextEEdgesCombination] < 0) {
                                edgesOrientationDistance[nextEdgesOrientation][nextEEdgesCombination] = (byte) (distance + 1);
                                nVisited++;
                            }
                        }
                    }
                }
            }
            distance++;
        }


        // phase 2
        cornersPermutationDistance = new byte[N_CORNERS_PERMUTATIONS][N_E_EDGES_PERMUTATIONS];
        for (int i = 0; i < cornersPermutationDistance.length; i++) {
            for (int j = 0; j < cornersPermutationDistance[i].length; j++) {
                cornersPermutationDistance[i][j] = -1;
            }
        }
        cornersPermutationDistance[0][0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_CORNERS_PERMUTATIONS * N_E_EDGES_PERMUTATIONS) {
            for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
                for (int j = 0; j < N_E_EDGES_PERMUTATIONS; j++) {
                    if (cornersPermutationDistance[i][j] == distance) {
                        for (int k = 0; k < N_MOVES_2; k++) {
                            int nextCornersPermutation = cornersPermutationMove[i][k];
                            int nextEEdgesPermutation = eEdgesPermutationMove[j][k];
                            if (cornersPermutationDistance[nextCornersPermutation][nextEEdgesPermutation] < 0) {
                                cornersPermutationDistance[nextCornersPermutation][nextEEdgesPermutation] = (byte) (distance + 1);
                                nVisited++;
                            }
                        }
                    }
                }
            }
            distance++;
        }


        uDEdgesPermutationDistance = new byte[N_U_D_EDGES_PERMUTATIONS][N_E_EDGES_PERMUTATIONS];
        for (int i = 0; i < uDEdgesPermutationDistance.length; i++) {
            for (int j = 0; j < uDEdgesPermutationDistance[i].length; j++) {
                uDEdgesPermutationDistance[i][j] = -1;
            }
        }
        uDEdgesPermutationDistance[0][0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_U_D_EDGES_PERMUTATIONS * N_E_EDGES_PERMUTATIONS) {
            for (int i = 0; i < N_U_D_EDGES_PERMUTATIONS; i++) {
                for (int j = 0; j < N_E_EDGES_PERMUTATIONS; j++) {
                    if (uDEdgesPermutationDistance[i][j] == distance) {
                        for (int k = 0; k < N_MOVES_2; k++) {
                            int nextUDEdgesPermutation = uDEdgesPermutationMove[i][k];
                            int nextEEdgesPermutation = eEdgesPermutationMove[j][k];
                            if (uDEdgesPermutationDistance[nextUDEdgesPermutation][nextEEdgesPermutation] < 0) {
                                uDEdgesPermutationDistance[nextUDEdgesPermutation][nextEEdgesPermutation] = (byte) (distance + 1);
                                nVisited++;
                            }
                        }
                    }
                }
            }
            distance++;
        }
    }

    // no first class functions, no coroutines, no pass by reference... i hate java
    private static int MAX_SOLUTION_LENGTH = 23;
    private static int MAX_PHASE_2_SOLUTION_LENGTH = 12;

    private static State inputState;
    private static int[] solution1;
    private static int[] solution2;

    private static int[] solution(State state) {
        inputState = state;

        boolean[] isEEdge = new boolean[12];
        for (int i = 0; i < isEEdge.length; i++) {
            isEEdge[i] = state.edgesPermutation[i] < 4;
        }

        int cornersOrientation = IndexMapping.zeroSumOrientationToIndex(state.cornersOrientation, 3);
        int edgesOrientation = IndexMapping.zeroSumOrientationToIndex(state.edgesOrientation, 2);
        int eEdgesCombination = IndexMapping.combinationToIndex(isEEdge, 4);

        for (int depth = 0; ; depth++) {
            solution1 = new int[depth];
            if (search1(cornersOrientation, edgesOrientation, eEdgesCombination, depth, 6)) {
                int[] solution = new int[solution1.length + solution2.length];

                for (int i = 0; i < solution1.length; i++) {
                    solution[i] = moves1[solution1[solution1.length - 1 - i]];
                }

                for (int i = 0; i < solution2.length; i++) {
                    solution[i + solution1.length] = moves2[solution2[solution2.length - 1 - i]];
                }

                return solution;
            }
        }
    }

    private static boolean search1(int cornersOrientation, int edgesOrientation, int eEdgesCombinations, int depth, int lastAxis) {
        if (depth == 0) {
            if (cornersOrientation == 0 && edgesOrientation == 0 && eEdgesCombinations == 0) {
                State state = inputState;
                for (int i = 0; i < solution1.length; i++) {
                    state = state.multiply(moves[moves1[solution1[solution1.length - 1 - i]]]);
                }

                return solution2(state, MAX_SOLUTION_LENGTH - solution1.length);
            }

            return false;
        }

        if (cornersOrientationDistance[cornersOrientation][eEdgesCombinations] <= depth &&
            edgesOrientationDistance[edgesOrientation][eEdgesCombinations] <= depth) {
            for (int i = 0; i < N_MOVES_1; i++) {
                if (moves1[i] / 3 != lastAxis) {
                    solution1[depth - 1] = i;
                    if (search1(cornersOrientationMove[cornersOrientation][i],
                                edgesOrientationMove[edgesOrientation][i],
                                eEdgesCombinationMove[eEdgesCombinations][i],
                                depth - 1,
                                moves1[i] / 3)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean solution2(State state, int maxDepth) {
        if (solution1.length > 0 && Arrays.binarySearch(moves2, solution1[0]) >= 0) {
            return false;
        }

        int cornersPermutation = IndexMapping.permutationToIndex(state.cornersPermutation);

        byte[] uDEdges = new byte[8];
        for (int i = 0; i < uDEdges.length; i++) {
            uDEdges[i] = (byte) (state.edgesPermutation[i + 4] - 4);
        }
        int uDEdgesPermutation = IndexMapping.permutationToIndex(uDEdges);

        byte[] eEdges = new byte[4];
        for (int i = 0; i < eEdges.length; i++) {
            eEdges[i] = state.edgesPermutation[i];
        }
        int eEdgesPermutation = IndexMapping.permutationToIndex(eEdges);

        for (int depth = 0; depth < Math.min(MAX_PHASE_2_SOLUTION_LENGTH, maxDepth); depth++) {
            solution2 = new int[depth];
            int lastAxis = solution1.length > 0 ? moves1[solution1[0]] / 3 : 6;
            if (search2(cornersPermutation, uDEdgesPermutation, eEdgesPermutation, depth, lastAxis)) {
                return true;
            }
        }

        return false;
    }

    private static boolean search2(int cornersPermutation, int uDEdgesPermutation, int eEdgesPermutation, int depth, int lastAxis) {
        if (depth == 0) {
            return cornersPermutation == 0 && uDEdgesPermutation == 0 && eEdgesPermutation == 0;
        }

        if (cornersPermutationDistance[cornersPermutation][eEdgesPermutation] <= depth &&
            uDEdgesPermutationDistance[uDEdgesPermutation][eEdgesPermutation] <= depth) {
            for (int i = 0; i < N_MOVES_2; i++) {
                if (moves2[i] / 3 != lastAxis) {
                    solution2[depth - 1] = i;
                    if (search2(cornersPermutationMove[cornersPermutation][i],
                                uDEdgesPermutationMove[uDEdgesPermutation][i],
                                eEdgesPermutationMove[eEdgesPermutation][i],
                                depth - 1,
                                moves2[i] / 3)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String[] generate(State state) {
        String[] inverseMoveNames = {
            "U'", "U2", "U",
            "D'", "D2", "D",
            "L'", "L2", "L",
            "R'", "R2", "R",
            "F'", "F2", "F",
            "B'", "B2", "B",
        };

        int[] solution = solution(state);

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoveNames[solution[solution.length - 1 - i]];
        }

        return sequence;
    }
}

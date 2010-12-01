package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RubiksDominoSolver {
    public static class State {
        public byte[] cornersPermutation;
        public byte[] edgesPermutation;

        public State(byte[] cornersPermutation, byte[] edgesPermutation) {
            this.cornersPermutation = cornersPermutation;
            this.edgesPermutation = edgesPermutation;
        }

        public State multiply(State move) {
            byte[] cornersPermutation = new byte[8];
            byte[] edgesPermutation = new byte[8];

            for (int i = 0; i < 8; i++) {
                cornersPermutation[i] = this.cornersPermutation[move.cornersPermutation[i]];
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
            }

            return new State(cornersPermutation, edgesPermutation);
        }
    }

    public static final int N_CORNERS_PERMUTATIONS = 40320;
    public static final int N_EDGES_PERMUTATIONS = 40320;
    public static final int N_MOVES = 10;

    private static State[] moves;
    private static int[] faces;

    private static int[][] cornersPermutationMove;
    private static int[][] edgesPermutationMove;

    private static byte[] cornersPermutationDistance;
    private static byte[] edgesPermutationDistance;

    static {
        State moveU = new State(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 });
        State moveD = new State(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 });
        State moveL = new State(new byte[] { 7, 1, 2, 4, 3, 5, 6, 0 }, new byte[] { 0, 1, 2, 7, 4, 5, 6, 3 });
        State moveR = new State(new byte[] { 0, 6, 5, 3, 4, 2, 1, 7 }, new byte[] { 0, 5, 2, 3, 4, 1, 6, 7 });
        State moveF = new State(new byte[] { 0, 1, 7, 6, 4, 5, 3, 2 }, new byte[] { 0, 1, 6, 3, 4, 5, 2, 7 });
        State moveB = new State(new byte[] { 5, 4, 2, 3, 1, 0, 6, 7 }, new byte[] { 4, 1, 2, 3, 0, 5, 6, 7 });

        moves = new State[] {
            moveU,
            moveU.multiply(moveU),
            moveU.multiply(moveU).multiply(moveU),
            moveD,
            moveD.multiply(moveD),
            moveD.multiply(moveD).multiply(moveD),
            moveL,
            moveR,
            moveF,
            moveB,
        };

        faces = new int[] {
            0, 0, 0,
            1, 1, 1,
            2,
            3,
            4,
            5,
        };

        // move tables
        cornersPermutationMove = new int[N_CORNERS_PERMUTATIONS][N_MOVES];
        for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
            State state = new State(IndexMapping.indexToPermutation(i, 8), new byte[8]);
            for (int j = 0; j < N_MOVES; j++) {
                cornersPermutationMove[i][j] =
                    IndexMapping.permutationToIndex(state.multiply(moves[j]).cornersPermutation);
            }
        }

        edgesPermutationMove = new int[N_EDGES_PERMUTATIONS][N_MOVES];
        for (int i = 0; i < N_EDGES_PERMUTATIONS; i++) {
            State state = new State(new byte[8], IndexMapping.indexToPermutation(i, 8));
            for (int j = 0; j < N_MOVES; j++) {
                edgesPermutationMove[i][j] =
                    IndexMapping.permutationToIndex(state.multiply(moves[j]).edgesPermutation);
            }
        }

        // prune tables
        cornersPermutationDistance = new byte[N_CORNERS_PERMUTATIONS];
        for (int i = 0; i < cornersPermutationDistance.length; i++) {
            cornersPermutationDistance[i] = -1;
        }
        cornersPermutationDistance[0] = 0;

        int distance = 0;
        int nVisited = 1;
        while (nVisited < N_CORNERS_PERMUTATIONS) {
            for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
                if (cornersPermutationDistance[i] == distance) {
                    for (int k = 0; k < N_MOVES; k++) {
                        int next = cornersPermutationMove[i][k];
                        if (cornersPermutationDistance[next] < 0) {
                            cornersPermutationDistance[next] = (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }
            distance++;
        }

        edgesPermutationDistance = new byte[N_CORNERS_PERMUTATIONS];
        for (int i = 0; i < edgesPermutationDistance.length; i++) {
            edgesPermutationDistance[i] = -1;
        }
        edgesPermutationDistance[0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_CORNERS_PERMUTATIONS) {
            for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
                if (edgesPermutationDistance[i] == distance) {
                    for (int k = 0; k < N_MOVES; k++) {
                        int next = edgesPermutationMove[i][k];
                        if (edgesPermutationDistance[next] < 0) {
                            edgesPermutationDistance[next] = (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }
            distance++;
        }
    }

    public static String[] solve(State state) {
        int cornersPermutation =
            IndexMapping.permutationToIndex(state.cornersPermutation);
        int edgesPermutation =
            IndexMapping.permutationToIndex(state.edgesPermutation);

        for (int depth = 0;; depth++) {
            ArrayList<Integer> solution = new ArrayList<Integer>();
            if (search(cornersPermutation, edgesPermutation, depth, solution, -1)) {
                String[] moveNames = { "U", "U2", "U'", "D", "D2", "D'", "L", "R", "F", "B" };

                String[] sequence = new String[solution.size()];
                for (int i = 0; i < sequence.length; i++) {
                    sequence[i] = moveNames[solution.get(i)];
                }

                return sequence;
            }
        }
    }

    private static boolean search(int cornersPermutation, int edgesPermutation, int depth, ArrayList<Integer> solution, int lastFace) {
        if (depth == 0) {
            return cornersPermutation == 0 && edgesPermutation == 0;
        }

        if (cornersPermutationDistance[cornersPermutation] <= depth &&
            edgesPermutationDistance[edgesPermutation] <= depth) {
            for (int i = 0; i < N_MOVES; i++) {
                if (faces[i] == lastFace) {
                    continue;
                }

                solution.add(i);
                if (search(
                    cornersPermutationMove[cornersPermutation][i],
                    edgesPermutationMove[edgesPermutation][i],
                    depth - 1,
                    solution,
                    faces[i])) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }

        return false;
    }

    public static String[] generate(State state) {
        String[] solution = solve(state);

        HashMap<String, String> inverseMoves = new HashMap<String, String>();
        inverseMoves.put("U",  "U'");
        inverseMoves.put("U2", "U2");
        inverseMoves.put("U'", "U");
        inverseMoves.put("D",  "D'");
        inverseMoves.put("D2", "D2");
        inverseMoves.put("D'", "D");
        inverseMoves.put("L",  "L");
        inverseMoves.put("R",  "R");
        inverseMoves.put("F",  "F");
        inverseMoves.put("B",  "B");

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoves.get(solution[solution.length - 1 - i]);
        }

        return sequence;
    }

    public static State getRandomState(Random random) {
        return new State(
            IndexMapping.indexToPermutation(
                random.nextInt(N_CORNERS_PERMUTATIONS), 8),
            IndexMapping.indexToPermutation(
                random.nextInt(N_EDGES_PERMUTATIONS), 8));
    }
}

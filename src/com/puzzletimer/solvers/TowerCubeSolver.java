package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TowerCubeSolver {
    public static class State {
        public byte[] cornersPermutation;
        public byte[] edgesPermutation;

        public State(byte[] cornersPermutation, byte[] edgesPermutation) {
            this.cornersPermutation = cornersPermutation;
            this.edgesPermutation = edgesPermutation;
        }

        public State multiply(State move) {
            // corners
            byte[] cornersPermutation = new byte[8];
            for (int i = 0; i < 8; i++) {
                cornersPermutation[i] = this.cornersPermutation[move.cornersPermutation[i]];
            }

            // edges
            byte[] edgesPermutation = new byte[4];
            for (int i = 0; i < 4; i++) {
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
            }

            return new State(cornersPermutation, edgesPermutation);
        }
    }

    private static int N_CORNERS_PERMUTATIONS = 40320;
    private static int N_EDGES_PERMUTATIONS = 24;
    private static int N_MOVES = 10;

    private static State[] moves;
    private static int[][] cornersPermutationMove;
    private static int[][] edgesPermutationMove;
    private static int distance[][];

    static {
        State moveU = new State(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 0, 1, 2, 3 });
        State moveD = new State(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 1, 2, 3 });
        State moveL = new State(new byte[] { 7, 1, 2, 4, 3, 5, 6, 0 }, new byte[] { 3, 1, 2, 0 });
        State moveR = new State(new byte[] { 0, 6, 5, 3, 4, 2, 1, 7 }, new byte[] { 0, 2, 1, 3 });
        State moveF = new State(new byte[] { 0, 1, 7, 6, 4, 5, 3, 2 }, new byte[] { 0, 1, 3, 2 });
        State moveB = new State(new byte[] { 5, 4, 2, 3, 1, 0, 6, 7 }, new byte[] { 1, 0, 2, 3 });

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

        // move tables
        cornersPermutationMove = new int[N_CORNERS_PERMUTATIONS][N_MOVES];
        for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
            State state = new State(IndexMapping.indexToPermutation(i, 8), new byte[4]);
            for (int j = 0; j < N_MOVES; j++) {
                cornersPermutationMove[i][j] =
                    IndexMapping.permutationToIndex(state.multiply(moves[j]).cornersPermutation);
            }
        }

        edgesPermutationMove = new int[N_EDGES_PERMUTATIONS][N_MOVES];
        for (int i = 0; i < N_EDGES_PERMUTATIONS; i++) {
            State state = new State(new byte[8], IndexMapping.indexToPermutation(i, 4));
            for (int j = 0; j < N_MOVES; j++) {
                edgesPermutationMove[i][j] =
                    IndexMapping.permutationToIndex(state.multiply(moves[j]).edgesPermutation);
            }
        }

        // distance table
        distance = new int[N_CORNERS_PERMUTATIONS][N_EDGES_PERMUTATIONS];
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance[i].length; j++) {
                distance[i][j] = -1;
            }
        }

        distance[0][0] = 0;

        int nVisited;
        int depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
                for (int j = 0; j < N_EDGES_PERMUTATIONS; j++) {
                    if (distance[i][j] == depth) {
                        for (int k = 0; k < N_MOVES; k++) {
                            int nextCornersPemutation = cornersPermutationMove[i][k];
                            int nextEdgesPemutation = edgesPermutationMove[j][k];

                            if (distance[nextCornersPemutation][nextEdgesPemutation] == -1) {
                                distance[nextCornersPemutation][nextEdgesPemutation] = depth + 1;
                                nVisited++;
                            }
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);
    }

    public static String[] solve(State state) {
        String[] moveNames = { "U", "U2", "U'", "D", "D2", "D'", "L", "R", "F", "B" };

        ArrayList<String> sequence = new ArrayList<String>();

        int cornersPermutationIndex =
            IndexMapping.permutationToIndex(state.cornersPermutation);
        int edgesPermutationIndex =
            IndexMapping.permutationToIndex(state.edgesPermutation);

        for (;;) {
            if (distance[cornersPermutationIndex][edgesPermutationIndex] == 0) {
                break;
            }

            for (int k = 0; k < N_MOVES; k++) {
                int nextCornersPermutationIndex =
                    cornersPermutationMove[cornersPermutationIndex][k];
                int nextEdgesPermutationIndex =
                    edgesPermutationMove[edgesPermutationIndex][k];

                if (distance[nextCornersPermutationIndex][nextEdgesPermutationIndex] ==
                    distance[cornersPermutationIndex][edgesPermutationIndex] - 1) {
                    sequence.add(moveNames[k]);
                    cornersPermutationIndex = nextCornersPermutationIndex;
                    edgesPermutationIndex = nextEdgesPermutationIndex;
                    break;
                }
            }
        }

        String[] sequenceArray = new String[sequence.size()];
        sequence.toArray(sequenceArray);

        return sequenceArray;
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
                random.nextInt(N_EDGES_PERMUTATIONS), 4));
    }
}

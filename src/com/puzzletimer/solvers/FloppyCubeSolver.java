package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FloppyCubeSolver {
    public static class State {
        public byte[] cornersPermutation;
        public byte[] edgesOrientation;

        public State(byte[] cornersPermutation, byte[] edgesOrientation) {
            this.cornersPermutation = cornersPermutation;
            this.edgesOrientation = edgesOrientation;
        }

        public State multiply(State move) {
            // corners
            byte[] cornersPermutation = new byte[4];

            for (int i = 0; i < 4; i++) {
                cornersPermutation[i] = this.cornersPermutation[move.cornersPermutation[i]];
            }

            // edges
            byte[] edgesOrientation = new byte[4];

            for (int i = 0; i < 4; i++) {
                edgesOrientation[i] = (byte) ((this.edgesOrientation[i] + move.edgesOrientation[i]) % 2);
            }

            return new State(cornersPermutation, edgesOrientation);
        }

        public static HashMap<String, State> moves;

        static {
            moves = new HashMap<String, State>();
            moves.put("U", new State(new byte[] { 1, 0, 2, 3 }, new byte[] { 1, 0, 0, 0 }));
            moves.put("R", new State(new byte[] { 0, 2, 1, 3 }, new byte[] { 0, 1, 0, 0 }));
            moves.put("D", new State(new byte[] { 0, 1, 3, 2 }, new byte[] { 0, 0, 1, 0 }));
            moves.put("L", new State(new byte[] { 3, 1, 2, 0 }, new byte[] { 0, 0, 0, 1 }));
        }
    }

    private static int N_CORNERS_PERMUTATION = 24;
    private static int N_EDGES_ORIENTATION = 16;

    private static int distance[][];

    static {
        distance = new int[N_CORNERS_PERMUTATION][N_EDGES_ORIENTATION];
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

            for (int i = 0; i < N_CORNERS_PERMUTATION; i++) {
                for (int j = 0; j < N_EDGES_ORIENTATION; j++) {
                    if (distance[i][j] == depth) {
                        State state = new State(
                            IndexMapping.indexToPermutation(i, 4),
                            IndexMapping.indexToOrientation(j, 2, 4));

                        for (String move : new String[] { "U", "R", "D", "L" }) {
                            State newState = state.multiply(State.moves.get(move));

                            int cornersPermutationIndex =
                                IndexMapping.permutationToIndex(newState.cornersPermutation);
                            int edgesOrientationIndex =
                                IndexMapping.orientationToIndex(newState.edgesOrientation, 2);

                            if (distance[cornersPermutationIndex][edgesOrientationIndex] == -1) {
                                distance[cornersPermutationIndex][edgesOrientationIndex] = depth + 1;
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
        ArrayList<String> sequence = new ArrayList<String>();

        for (;;) {
            int cornersPermutationIndex =
                IndexMapping.permutationToIndex(state.cornersPermutation);
            int edgesOrientationIndex =
                IndexMapping.orientationToIndex(state.edgesOrientation, 2);

            if (distance[cornersPermutationIndex][edgesOrientationIndex] == 0) {
                break;
            }

            for (String move : new String[] { "U", "D", "L", "R" }) {
                State nextState = state.multiply(State.moves.get(move));

                int nextCornersPermutationIndex =
                    IndexMapping.permutationToIndex(nextState.cornersPermutation);
                int nextEdgesOrientationIndex =
                    IndexMapping.orientationToIndex(nextState.edgesOrientation, 2);

                if (distance[nextCornersPermutationIndex][nextEdgesOrientationIndex] ==
                    distance[cornersPermutationIndex][edgesOrientationIndex] - 1) {
                    sequence.add(move);
                    state = nextState;
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

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = solution[solution.length - 1 - i];
        }

        return sequence;
    }

    public static State getRandomState(Random random) {
        for (;;) {
            int cornersPermutationIndex =
                random.nextInt(N_CORNERS_PERMUTATION);
            int edgesOrientationIndex =
                random.nextInt(N_EDGES_ORIENTATION);

            if (distance[cornersPermutationIndex][edgesOrientationIndex] >= 0) {
                byte[] cornersPermutation =
                    IndexMapping.indexToPermutation(cornersPermutationIndex, 4);
                byte[] edgesOrientation =
                    IndexMapping.indexToOrientation(edgesOrientationIndex, 2, 4);

                return new State(cornersPermutation, edgesOrientation);
            }
        }
    }
}

package com.puzzletimer.solvers;

import java.util.HashMap;
import java.util.Random;

import com.puzzletimer.solvers.RubiksCubeSolver.State;

public class RubiksCubeRUSolver {
    // moves
    private static String[] moveNames;
    private static State[] moves;

    static {
        moveNames = new String[] {
            "U", "U2", "U'",
            "R", "R2", "R'",
        };

        moves = new State[moveNames.length];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = RubiksCubeSolver.State.moves.get(moveNames[i]);
        }
    }

    // constants
    private static int N_CORNERS_PERMUTATIONS = 720;
    private static int N_CORNERS_ORIENTATIONS = 729;
    private static int N_EDGES_PERMUTATIONS = 5040;
    private static int N_EDGES_ORIENTATIONS = 128;

    private static int goalCornersPermutation;
    private static int goalCornersOrientation;
    private static int goalEdgesPermutation;
    private static int goalEdgesOrientation;

    static {
        int[] goalIndices =
            stateToIndices(RubiksCubeSolver.State.id);

        goalCornersPermutation = goalIndices[0];
        goalCornersOrientation = goalIndices[1];
        goalEdgesPermutation = goalIndices[2];
        goalEdgesOrientation = goalIndices[3];
    }

    // move tables
    private static int[][] cornersPermutationMove;
    private static int[][] cornersOrientationMove;
    private static int[][] edgesPermutationMove;
    private static int[][] edgesOrientationMove;

    static {
        // corners permutation
        cornersPermutationMove = new int[N_CORNERS_PERMUTATIONS][moves.length];
        for (int i = 0; i < N_CORNERS_PERMUTATIONS; i++) {
            State state = indicesToState(new int[] { i, 0, 0, 0 });
            for (int j = 0; j < moves.length; j++) {
                int[] indices = stateToIndices(state.multiply(moves[j]));
                cornersPermutationMove[i][j] = indices[0];
            }
        }

        // corners orientation
        cornersOrientationMove = new int[N_CORNERS_ORIENTATIONS][moves.length];
        for (int i = 0; i < N_CORNERS_ORIENTATIONS; i++) {
            State state = indicesToState(new int[] { 0, i, 0, 0 });
            for (int j = 0; j < moves.length; j++) {
                int[] indices = stateToIndices(state.multiply(moves[j]));
                cornersOrientationMove[i][j] = indices[1];
            }
        }

        // edges permutation
        edgesPermutationMove = new int[N_EDGES_PERMUTATIONS][moves.length];
        for (int i = 0; i < N_EDGES_PERMUTATIONS; i++) {
            State state = indicesToState(new int[] { 0, 0, i, 0 });
            for (int j = 0; j < moves.length; j++) {
                int[] indices = stateToIndices(state.multiply(moves[j]));
                edgesPermutationMove[i][j] = indices[2];
            }
        }

        // edges orientation
        edgesOrientationMove = new int[N_EDGES_ORIENTATIONS][moves.length];
        for (int i = 0; i < N_EDGES_ORIENTATIONS; i++) {
            State state = indicesToState(new int[] { 0, 0, 0, i });
            for (int j = 0; j < moves.length; j++) {
                int[] indices = stateToIndices(state.multiply(moves[j]));
                edgesOrientationMove[i][j] = indices[3];
            }
        }
    }

    private static int[] stateToIndices(State state) {
        // corners
        boolean[] selectedCorners = {
            true,  true,  true,  true,
            false, true,  true,  false,
        };

        byte[] cornersMapping = {
             0,  1,  2,  3,
            -1,  5,  6, -1,
        };

        byte[] cornersPermutation = new byte[6];
        byte[] cornersOrientation = new byte[6];
        int next = 0;
        for (int i = 0; i < state.cornersPermutation.length; i++) {
            if (selectedCorners[i]) {
                cornersPermutation[next] = cornersMapping[state.cornersPermutation[i]];
                cornersOrientation[next] = state.cornersOrientation[i];
                next++;
            }
        }
        int cornersPermutationIndex =
            IndexMapping.permutationToIndex(cornersPermutation);
        int cornersOrientationIndex =
            IndexMapping.orientationToIndex(cornersOrientation, 3);

        // edges
        boolean[] selectedEdges = {
            false, true,  true,  false,
            true,  true,  true,  true,
            false, true,  false, false,
        };

        byte[] edgesMapping = {
            -1,  0,  1, -1,
             2,  3,  4,  5,
            -1,  6, -1, -1,
        };

        byte[] edgesPermutation = new byte[7];
        byte[] edgesOrientation = new byte[7];
        next = 0;
        for (int i = 0; i < state.edgesPermutation.length; i++) {
            if (selectedEdges[i]) {
                edgesPermutation[next] = edgesMapping[state.edgesPermutation[i]];
                edgesOrientation[next] = state.edgesOrientation[i];
                next++;
            }
        }
        int edgesPermutationIndex =
            IndexMapping.permutationToIndex(edgesPermutation);
        int edgesOrientationIndex =
            IndexMapping.orientationToIndex(edgesOrientation, 2);

        return new int[] {
            cornersPermutationIndex,
            cornersOrientationIndex,
            edgesPermutationIndex,
            edgesOrientationIndex,
        };
    }

    private static State indicesToState(int[] indices) {
        // corners
        boolean[] combination = {
            true,  true,  true,  true,
            false, true,  true,  false,
        };
        byte[] permutation =
            IndexMapping.indexToPermutation(indices[0], 6);
        byte[] orientation =
            IndexMapping.indexToOrientation(indices[1], 3, 6);

        byte[] selectedCorners = { 0, 1, 2, 3, 5, 6 };
        int nextSelectedCornerIndex = 0;
        byte[] otherCorners = { 4, 7 };
        int nextOtherCornerIndex = 0;

        byte[] cornersPermutation = new byte[8];
        byte[] cornersOrientation = new byte[8];
        for (int i = 0; i < cornersPermutation.length; i++) {
            if (combination[i]) {
                cornersPermutation[i] = selectedCorners[permutation[nextSelectedCornerIndex]];
                cornersOrientation[i] = orientation[nextSelectedCornerIndex];
                nextSelectedCornerIndex++;
            } else {
                cornersPermutation[i] = otherCorners[nextOtherCornerIndex];
                cornersOrientation[i] = 0;
                nextOtherCornerIndex++;
            }
        }

        // edges
        combination = new boolean[] {
            false, true,  true,  false,
            true,  true,  true,  true,
            false, true,  false, false,
        };
        permutation =
            IndexMapping.indexToPermutation(indices[2], 7);
        orientation =
            IndexMapping.indexToOrientation(indices[3], 2, 7);

        byte[] selectedEdges = { 1, 2, 4, 5, 6, 7, 9 };
        int nextSelectedEdgeIndex = 0;
        byte[] otherEdges = { 0, 3, 8, 10, 11 };
        int nextOtherEdgeIndex = 0;

        byte[] edgesPermutation = new byte[12];
        byte[] edgesOrientation = new byte[12];
        for (int i = 0; i < edgesPermutation.length; i++) {
            if (combination[i]) {
                edgesPermutation[i] = selectedEdges[permutation[nextSelectedEdgeIndex]];
                edgesOrientation[i] = orientation[nextSelectedEdgeIndex];
                nextSelectedEdgeIndex++;
            } else {
                edgesPermutation[i] = otherEdges[nextOtherEdgeIndex];
                edgesOrientation[i] = 0;
                nextOtherEdgeIndex++;
            }
        }

        return new State(
            cornersPermutation,
            cornersOrientation,
            edgesPermutation,
            edgesOrientation);
    }

    // distance tables
    private static byte[][] cornersDistance;
    private static byte[][] edgesDistance;


    static {
        // corners
        cornersDistance = new byte[N_CORNERS_PERMUTATIONS][N_CORNERS_ORIENTATIONS];
        for (int i = 0; i < cornersDistance.length; i++) {
            for (int j = 0; j < cornersDistance[i].length; j++) {
                cornersDistance[i][j] = -1;
            }
        }
        cornersDistance[goalCornersPermutation][goalCornersOrientation] = 0;

        int distance = 0;
        int nVisited;
        do {
            nVisited = 0;

            for (int i = 0; i < cornersDistance.length; i++) {
                for (int j = 0; j < cornersDistance[i].length; j++) {
                    if (cornersDistance[i][j] != distance) {
                        continue;
                    }

                    for (int k = 0; k < cornersPermutationMove[i].length; k++) {
                        int nextPermutation = cornersPermutationMove[i][k];
                        int nextOrientation = cornersOrientationMove[j][k];
                        if (cornersDistance[nextPermutation][nextOrientation] < 0) {
                            cornersDistance[nextPermutation][nextOrientation] =
                                (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }

            distance++;
        } while (nVisited > 0);

        // edges
        edgesDistance = new byte[N_EDGES_PERMUTATIONS][N_EDGES_ORIENTATIONS];
        for (int i = 0; i < edgesDistance.length; i++) {
            for (int j = 0; j < edgesDistance[i].length; j++) {
                edgesDistance[i][j] = -1;
            }
        }
        edgesDistance[goalEdgesPermutation][goalEdgesOrientation] = 0;

        distance = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < edgesDistance.length; i++) {
                for (int j = 0; j < edgesDistance[i].length; j++) {
                    if (edgesDistance[i][j] != distance) {
                        continue;
                    }

                    for (int k = 0; k < edgesPermutationMove[i].length; k++) {
                        int nextPermutation = edgesPermutationMove[i][k];
                        int nextOrientation = edgesOrientationMove[j][k];
                        if (edgesDistance[nextPermutation][nextOrientation] < 0) {
                            edgesDistance[nextPermutation][nextOrientation] =
                                (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }

            distance++;
        } while (nVisited > 0);

    }

    public static String[] solve(State state) {
        int[] indices = stateToIndices(state);

        for (int depth = 0; ; depth++) {
            int[] solution = new int[depth];

            if (search(
                    indices[0],
                    indices[1],
                    indices[2],
                    indices[3],
                    depth,
                    solution)) {
                String[] sequence = new String[solution.length];
                for (int i = 0; i < sequence.length; i++) {
                    sequence[i] = moveNames[solution[i]];
                }

                return sequence;
            }
        }
    }

    private static boolean search(
            int cornersPermutation,
            int cornersOrientation,
            int edgesPermutation,
            int edgesOrientation,
            int depth,
            int[] solution) {
        if (depth == 0) {
            return cornersPermutation == goalCornersPermutation &&
                   cornersOrientation == goalCornersOrientation &&
                   edgesPermutation == goalEdgesPermutation &&
                   edgesOrientation == goalEdgesOrientation;
        }

        if (cornersDistance[cornersPermutation][cornersOrientation] > depth ||
            edgesDistance[edgesPermutation][edgesOrientation] > depth) {
            return false;
        }

        for (int i = 0; i < moves.length; i++) {
            if (solution.length - depth > 0) {
                if (solution[solution.length - depth - 1] / 3 == i / 3) {
                    continue;
                }
            }

            solution[solution.length - depth] = i;
            if (search(
                    cornersPermutationMove[cornersPermutation][i],
                    cornersOrientationMove[cornersOrientation][i],
                    edgesPermutationMove[edgesPermutation][i],
                    edgesOrientationMove[edgesOrientation][i],
                    depth - 1,
                    solution)) {
                return true;
            }
        }

        return false;
    }

    public static String[] generate(State state) {
        HashMap<String, String> inverseMoveNames = new HashMap<String, String>();
        inverseMoveNames.put("U",  "U'");
        inverseMoveNames.put("U2", "U2");
        inverseMoveNames.put("U'", "U");
        inverseMoveNames.put("R",  "R'");
        inverseMoveNames.put("R2", "R2");
        inverseMoveNames.put("R'", "R");

        String[] solution = solve(state);

        String[] sequence = new String[solution.length];
        for (int i = 0; i < solution.length; i++) {
            sequence[i] = inverseMoveNames.get(solution[solution.length - i - 1]);
        }

        return sequence;
    }

    public static State getRandomState(Random random) {
        for (;;) {
            int cornersPermutation = random.nextInt(N_CORNERS_PERMUTATIONS);
            int cornersOrientation = random.nextInt(N_CORNERS_ORIENTATIONS);
            int edgesPermutation = random.nextInt(N_EDGES_PERMUTATIONS);
            int edgesOrientation = random.nextInt(N_EDGES_ORIENTATIONS);

            if (cornersDistance[cornersPermutation][cornersOrientation] < 0 ||
                edgesDistance[edgesPermutation][edgesOrientation] < 0) {
                continue;
            }

            State state = indicesToState(
                new int[] {
                    cornersPermutation,
                    cornersOrientation,
                    edgesPermutation,
                    edgesOrientation,
                });

            if (permutationSign(state.cornersPermutation) ==
                permutationSign(state.edgesPermutation)) {
                return state;
            }
        }
    }

    private static int permutationSign(byte[] permutation) {
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
}

package com.puzzletimer.solvers;

public class PyraminxSolver {
    public static class State {
        public byte[] verticesOrientation;
        public byte[] edgesPermutation;
        public byte[] edgesOrientation;

        public State(byte[] verticesOrientation, byte[] edgesPermutation, byte[] edgesOrientation) {
            this.verticesOrientation = verticesOrientation;
            this.edgesPermutation = edgesPermutation;
            this.edgesOrientation = edgesOrientation;
        }

        public State multiply(State move) {
            byte[] resultVerticesOrientation = new byte[4];
            byte[] resultEdgesPermutation = new byte[6];
            byte[] resultEdgesOrientation = new byte[6];

            for (int i = 0; i < 4; i++) {
                resultVerticesOrientation[i] = (byte) ((this.verticesOrientation[i] + move.verticesOrientation[i]) % 3);
            }

            for (int i = 0; i < 6; i++) {
                resultEdgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
                resultEdgesOrientation[i] = (byte) ((this.edgesOrientation[move.edgesPermutation[i]] + move.edgesOrientation[i]) % 2);
            }

            return new State(resultVerticesOrientation, resultEdgesPermutation, resultEdgesOrientation);
        }
    }

    public static final int N_TIPS_ORIERNTATIONS = 81;
    public static final int N_VERTICES_ORIERNTATIONS = 81;
    public static final int N_EDGES_PERMUTATIONS = 360;
    public static final int N_EDGES_ORIENTATIONS = 32;
    public static final int N_MOVES = 8;

    private static State[] moves;

    private static int[][] verticesOrientationMove;
    private static int[][] edgesPermutationMove;
    private static int[][] edgesOrientationMove;

    private static byte[] verticesOrientationDistance;
    private static byte[] edgesPermutationDistance;
    private static byte[] edgesOrientationDistance;

    static {
        State moveU = new State(new byte[] { 1, 0, 0, 0 }, new byte[] { 2, 0, 1, 3, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0 });
        State moveL = new State(new byte[] { 0, 1, 0, 0 }, new byte[] { 0, 1, 5, 3, 2, 4 }, new byte[] { 0, 0, 1, 0, 0, 1 });
        State moveR = new State(new byte[] { 0, 0, 1, 0 }, new byte[] { 0, 4, 2, 1, 3, 5 }, new byte[] { 0, 1, 0, 0, 1, 0 });
        State moveB = new State(new byte[] { 0, 0, 0, 1 }, new byte[] { 3, 1, 2, 5, 4, 0 }, new byte[] { 1, 0, 0, 1, 0, 0 });

        moves = new State[] {
            moveU,
            moveU.multiply(moveU),
            moveL,
            moveL.multiply(moveL),
            moveR,
            moveR.multiply(moveR),
            moveB,
            moveB.multiply(moveB),
        };

        // move tables
        verticesOrientationMove = new int[N_VERTICES_ORIERNTATIONS][N_MOVES];
        for (int i = 0; i < N_VERTICES_ORIERNTATIONS; i++) {
            State state = new State(IndexMapping.indexToOrientation(i, 3, 4), new byte[6], new byte[6]);
            for (int j = 0; j < N_MOVES; j++) {
                verticesOrientationMove[i][j] = IndexMapping.orientationToIndex(state.multiply(moves[j]).verticesOrientation, 3);
            }
        }

        edgesPermutationMove = new int[N_EDGES_PERMUTATIONS][N_MOVES];
        for (int i = 0; i < N_EDGES_PERMUTATIONS; i++) {
            State state = new State(new byte[4], IndexMapping.indexToEvenPermutation(i, 6), new byte[6]);
            for (int j = 0; j < N_MOVES; j++) {
                edgesPermutationMove[i][j] = IndexMapping.evenPermutationToIndex(state.multiply(moves[j]).edgesPermutation);
            }
        }

        edgesOrientationMove = new int[N_EDGES_ORIENTATIONS][N_MOVES];
        for (int i = 0; i < N_EDGES_ORIENTATIONS; i++) {
            State state = new State(new byte[4], new byte[6], IndexMapping.indexToZeroSumOrientation(i, 2, 6));
            for (int j = 0; j < N_MOVES; j++) {
                edgesOrientationMove[i][j] = IndexMapping.zeroSumOrientationToIndex(state.multiply(moves[j]).edgesOrientation, 2);
            }
        }

        // prune tables
        verticesOrientationDistance = new byte[N_VERTICES_ORIERNTATIONS];
        for (int i = 0; i < verticesOrientationDistance.length; i++) {
            verticesOrientationDistance[i] = -1;
        }
        verticesOrientationDistance[0] = 0;

        int distance = 0;
        int nVisited = 1;
        while (nVisited < N_VERTICES_ORIERNTATIONS) {
            for (int i = 0; i < N_VERTICES_ORIERNTATIONS; i++) {
                if (verticesOrientationDistance[i] == distance) {
                    for (int k = 0; k < N_MOVES; k++) {
                        int next = verticesOrientationMove[i][k];
                        if (verticesOrientationDistance[next] < 0) {
                            verticesOrientationDistance[next] = (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }
            distance++;
        }

        edgesPermutationDistance = new byte[N_EDGES_PERMUTATIONS];
        for (int i = 0; i < edgesPermutationDistance.length; i++) {
            edgesPermutationDistance[i] = -1;
        }
        edgesPermutationDistance[0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_EDGES_PERMUTATIONS) {
            for (int i = 0; i < N_EDGES_PERMUTATIONS; i++) {
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

        edgesOrientationDistance = new byte[N_EDGES_ORIENTATIONS];
        for (int i = 0; i < edgesOrientationDistance.length; i++) {
            edgesOrientationDistance[i] = -1;
        }
        edgesOrientationDistance[0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_EDGES_ORIENTATIONS) {
            for (int i = 0; i < N_EDGES_ORIENTATIONS; i++) {
                if (edgesOrientationDistance[i] == distance) {
                    for (int k = 0; k < N_MOVES; k++) {
                        int next = edgesOrientationMove[i][k];
                        if (edgesOrientationDistance[next] < 0) {
                            edgesOrientationDistance[next] = (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }
            distance++;
        }
    }

    private static int[] solution(int verticesOrientation, int edgesPermutation, int edgesOrientation) {
        for (int depth = 0;; depth++) {
            int[] solution = new int[depth];
            if (search(verticesOrientation, edgesPermutation, edgesOrientation, depth, solution, 4)) {
                return solution;
            }
        }
    }

    private static boolean search(int verticesOrientation, int edgesPermutation, int edgesOrientation, int depth, int[] solution, int axis) {
        if (depth == 0) {
            return verticesOrientation == 0 && edgesPermutation == 0 && edgesOrientation == 0;
        }

        if (verticesOrientationDistance[verticesOrientation] <= depth &&
            edgesPermutationDistance[edgesPermutation] <= depth &&
            edgesOrientationDistance[edgesOrientation] <= depth) {
            for (int i = 0; i < N_MOVES; i++) {
                if (i / 2 != axis) {
                    solution[depth - 1] = i;
                    if (search(verticesOrientationMove[verticesOrientation][i],
                               edgesPermutationMove[edgesPermutation][i],
                               edgesOrientationMove[edgesOrientation][i],
                               depth - 1,
                               solution,
                               i / 2)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String[] generate(int tipsOrientation, int verticesOrientation, int edgesPermutation, int edgesOrientation) {
         // tips
        byte[] tips = IndexMapping.indexToOrientation(tipsOrientation, 3, 4);
        byte[] vertices = IndexMapping.indexToOrientation(verticesOrientation, 3, 4);

        int nTipMoves = 0;
        int[] tipsSolution = new int[4];
        for (int i = 0; i < tipsSolution.length; i++) {
            tipsSolution[i] = (tips[i] + vertices[i]) % 3;
            if (tipsSolution[i] != 0) {
                nTipMoves++;
            }
        }

        // body
        int[] bodySolution = solution(verticesOrientation, edgesPermutation, edgesOrientation);

        // scramble sequence
        String[] sequence = new String[nTipMoves + bodySolution.length];

        String[] inverseTipMoveNames = {
            "u'", "u",
            "l'", "l",
            "r'", "r",
            "b'", "b",
        };

        int pSequence = 0;
        for (int i = 0; i < tipsSolution.length; i++) {
            if (tipsSolution[i] != 0) {
                sequence[pSequence++] = inverseTipMoveNames[2 * i + tipsSolution[i] - 1];
            }
        }

        String[] inverseMoveNames = {
            "U'", "U",
            "L'", "L",
            "R'", "R",
            "B'", "B",
        };

        for (int i = 0; i < bodySolution.length; i++) {
            sequence[pSequence++] = inverseMoveNames[bodySolution[i]];
        }

        return sequence;
    }
}

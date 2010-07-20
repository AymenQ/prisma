package com.puzzletimer.solvers;

class Cube {
    public byte[] permutation;
    public byte[] orientation;

    public Cube(byte[] permutation, byte[] orientation) {
        this.orientation = orientation;
        this.permutation = permutation;
    }

    public Cube multiply(Cube move) {
        byte[] resultPermutation = new byte[8];
        byte[] resultOrientation = new byte[8];

        for (int i = 0; i < 8; i++) {
            resultPermutation[i] = this.permutation[move.permutation[i]];
            resultOrientation[i] = (byte) ((this.orientation[move.permutation[i]] + move.orientation[i]) % 3);
        }

        return new Cube(resultPermutation, resultOrientation);
    }
}

public class RubiksPocketCubeSolver {
    public static final int N_PERMUTATIONS = 40320;
    public static final int N_ORIENTATIONS = 2187;
    public static final int N_MOVES = 18;

    private static Cube[] moves;

    private static int[][] permutationMove;
    private static int[][] orientationMove;

    private static byte[] permutationDistance;
    private static byte[] orientationDistance;

    static {
        moves = new Cube[] {
            new Cube(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 2, 3, 0, 1, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 1, 2, 3, 0, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 0, 1, 2, 3, 6, 7, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 0, 1, 2, 3, 7, 4, 5, 6 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 4, 1, 2, 0, 7, 5, 6, 3 }, new byte[] { 2, 0, 0, 1, 1, 0, 0, 2 }),
            new Cube(new byte[] { 7, 1, 2, 4, 3, 5, 6, 0 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 3, 1, 2, 7, 0, 5, 6, 4 }, new byte[] { 2, 0, 0, 1, 1, 0, 0, 2 }),
            new Cube(new byte[] { 0, 2, 6, 3, 4, 1, 5, 7 }, new byte[] { 0, 1, 2, 0, 0, 2, 1, 0 }),
            new Cube(new byte[] { 0, 6, 5, 3, 4, 2, 1, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 0, 5, 1, 3, 4, 6, 2, 7 }, new byte[] { 0, 1, 2, 0, 0, 2, 1, 0 }),
            new Cube(new byte[] { 0, 1, 3, 7, 4, 5, 2, 6 }, new byte[] { 0, 0, 1, 2, 0, 0, 2, 1 }),
            new Cube(new byte[] { 0, 1, 7, 6, 4, 5, 3, 2 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 0, 1, 6, 2, 4, 5, 7, 3 }, new byte[] { 0, 0, 1, 2, 0, 0, 2, 1 }),
            new Cube(new byte[] { 1, 5, 2, 3, 0, 4, 6, 7 }, new byte[] { 1, 2, 0, 0, 2, 1, 0, 0 }),
            new Cube(new byte[] { 5, 4, 2, 3, 1, 0, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
            new Cube(new byte[] { 4, 0, 2, 3, 5, 1, 6, 7 }, new byte[] { 1, 2, 0, 0, 2, 1, 0, 0 }),
        };

        // move tables
        permutationMove = new int[N_PERMUTATIONS][N_MOVES];
        for (int i = 0; i < N_PERMUTATIONS; i++) {
            Cube cube = new Cube(IndexMapping.indexToPermutation(i, 8), new byte[8]);
            for (int j = 0; j < N_MOVES; j++) {
                permutationMove[i][j] = IndexMapping.permutationToIndex(cube.multiply(moves[j]).permutation);
            }
        }

        orientationMove = new int[N_ORIENTATIONS][N_MOVES];
        for (int i = 0; i < N_ORIENTATIONS; i++) {
            Cube cube = new Cube(new byte[8], IndexMapping.indexToZeroSumOrientation(i, 3, 8));
            for (int j = 0; j < N_MOVES; j++) {
                orientationMove[i][j] = IndexMapping.zeroSumOrientationToIndex(cube.multiply(moves[j]).orientation, 3);
            }
        }

        // prune tables
        permutationDistance = new byte[N_PERMUTATIONS];
        for (int i = 0; i < permutationDistance.length; i++) {
            permutationDistance[i] = -1;
        }
        permutationDistance[0] = 0;

        int distance = 0;
        int nVisited = 1;
        while (nVisited < N_PERMUTATIONS) {
            for (int i = 0; i < N_PERMUTATIONS; i++) {
                if (permutationDistance[i] == distance) {
                    for (int k = 0; k < N_MOVES; k++) {
                        int next = permutationMove[i][k];
                        if (permutationDistance[next] < 0) {
                            permutationDistance[next] = (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }
            distance++;
        }

        orientationDistance = new byte[N_ORIENTATIONS];
        for (int i = 0; i < orientationDistance.length; i++) {
            orientationDistance[i] = -1;
        }
        orientationDistance[0] = 0;

        distance = 0;
        nVisited = 1;
        while (nVisited < N_ORIENTATIONS) {
            for (int i = 0; i < N_ORIENTATIONS; i++) {
                if (orientationDistance[i] == distance) {
                    for (int k = 0; k < N_MOVES; k++) {
                        int next = orientationMove[i][k];
                        if (orientationDistance[next] < 0) {
                            orientationDistance[next] = (byte) (distance + 1);
                            nVisited++;
                        }
                    }
                }
            }
            distance++;
        }
    }

    private static int[] solution(int permutation, int orientation) {
        for (int depth = 0;; depth++) {
            int[] solution = new int[depth];
            if (search(permutation, orientation, depth, solution, 6)) {
                return solution;
            }
        }
    }

    private static boolean search(int permutation, int orientation, int depth, int[] solution, int axis) {
        if (depth == 0) {
            return permutation == 0 && orientation == 0;
        }

        if (permutationDistance[permutation] <= depth && orientationDistance[orientation] <= depth) {
            for (int i = 0; i < N_MOVES; i++) {
                if (i / 3 != axis) {
                    solution[depth - 1] = i;
                    if (search(permutationMove[permutation][i], orientationMove[orientation][i], depth - 1, solution, i / 3)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String[] generate(int permutation, int orientation) {
        String[] inverseMoveNames = {
            "U'", "U2", "U",
            "D'", "D2", "D",
            "L'", "L2", "L",
            "R'", "R2", "R",
            "F'", "F2", "F",
            "B'", "B2", "B",
        };

        int[] solution = solution(permutation, orientation);

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoveNames[solution[i]];
        }

        return sequence;
    }
}

package com.puzzletimer.solvers;

public class RubiksCubeCrossSolver {
    private static class State {
        public boolean[] combination;
        public byte[] permutation;
        public byte[] orientation;

        public State(boolean[] combination, byte[] permutation, byte[] orientation) {
            this.combination = combination;
            this.permutation = permutation;
            this.orientation = orientation;
        }

        public State multiply(Move move) {
            // edges position
            byte[] position = new byte[this.permutation.length];
            int next = 0;
            for (int i = 0; i < this.combination.length; i++) {
                if (this.combination[i]) {
                    position[this.permutation[next++]] = (byte) i;
                }
            }

            // apply move
            byte[] resultPosition = new byte[this.permutation.length];
            byte[] resultOrientation = new byte[this.orientation.length];
            for (int i = 0; i < position.length; i++) {
                resultPosition[i] = move.permutation[position[i]];
                resultOrientation[i] = (byte) ((this.orientation[i] + move.orientation[position[i]]) % 2);
            }

            // retrieve result combination
            boolean[] resultCombination = new boolean[this.combination.length];
            for (int i = 0; i < resultPosition.length; i++) {
                resultCombination[resultPosition[i]] = true;
            }

            // retrieve result permutation
            byte[] resultPermutation = new byte[this.permutation.length];
            next = 0;
            for (int i = 0; i < resultCombination.length; i++) {
                if (resultCombination[i]) {
                    for (int j = 0; j < resultPosition.length; j++) {
                        if (resultPosition[j] == i) {
                            resultPermutation[next++] = (byte) j;
                            break;
                        }
                    }
                }
            }

            return new State(resultCombination, resultPermutation, resultOrientation);
        }
    }

    private static class Move {
        public byte[] permutation;
        public byte[] orientation;

        public Move(byte[] permutation, byte[] orientation) {
            this.permutation = permutation;
            this.orientation = orientation;
        }

        public Move multiply(Move move) {
            byte[] permutation = new byte[12];
            byte[] orientation = new byte[12];

            for (int i = 0; i < 12; i++) {
                permutation[i] = move.permutation[this.permutation[i]];
                orientation[i] = (byte) ((move.orientation[this.permutation[i]] + this.orientation[i]) % 2);
            }

            return new Move(permutation, orientation);
        }
    }

    // constants
    private static final int N_MOVES = 18;
    private static final int N_COMBINATIONS = 495;
    private static final int N_PERMUTATIONS = 24;
    private static final int N_ORIENTATIONS = 16;

    // moves
    private static Move[] moves;

    static {
        Move moveU = new Move(new byte[] { 1, 2, 3, 0, 4, 5, 6, 7, 8, 9, 10, 11 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        Move moveD = new Move(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 11, 8, 9, 10 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        Move moveL = new Move(new byte[] { 0, 1, 2, 7, 3, 5, 6, 11, 8, 9, 10, 4 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        Move moveR = new Move(new byte[] { 0, 5, 2, 3, 4, 9, 1, 7, 8, 6, 10, 11 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        Move moveF = new Move(new byte[] { 0, 1, 6, 3, 4, 5, 10, 2, 8, 9, 7, 11 }, new byte[] { 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0 });
        Move moveB = new Move(new byte[] { 4, 1, 2, 3, 8, 0, 6, 7, 5, 9, 10, 11 }, new byte[] { 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0 });

        moves = new Move[] {
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
    }

    // distance table
    public static byte[][][] distance;

    static {
        distance = new byte[N_COMBINATIONS][N_PERMUTATIONS][N_ORIENTATIONS];

        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance[i].length; j++) {
                for (int k = 0; k < distance[i][j].length; k++) {
                    distance[i][j][k] = -1;
                }
            }
        }
        distance[0][0][0] = 0;

        int depth = 0;
        int nVisited = 1;
        while (nVisited < N_COMBINATIONS * N_PERMUTATIONS * N_ORIENTATIONS) {
            for (int i = 0; i < N_COMBINATIONS; i++) {
                boolean[] combination = IndexMapping.indexToCombination(i, 4, 12);

                for (int j = 0; j < N_PERMUTATIONS; j++) {
                    byte[] permutation = IndexMapping.indexToPermutation(j, 4);

                    for (int k = 0; k < N_ORIENTATIONS; k++) {
                        byte[] orientation = IndexMapping.indexToOrientation(k, 2, 4);

                        if (distance[i][j][k] == depth) {
                            State state = new State(combination, permutation, orientation);

                            for (int m = 0; m < N_MOVES; m++) {
                                State result = state.multiply(moves[m]);

                                int combinationIndex = IndexMapping.combinationToIndex(result.combination, 4);
                                int permutationIndex = IndexMapping.permutationToIndex(result.permutation);
                                int orientationIndex = IndexMapping.orientationToIndex(result.orientation, 2);

                                if (distance[combinationIndex][permutationIndex][orientationIndex] < 0) {
                                    distance[combinationIndex][permutationIndex][orientationIndex] = (byte) (depth + 1);
                                    nVisited++;
                                }
                            }
                        }
                    }
                }
            }

            depth++;
        }
    }

    public static String[] generate(int combination, int permutation, int orientation)
    {
        String[] inverseMoveNames = {
            "U'", "U2", "U",
            "D'", "D2", "D",
            "L'", "L2", "L",
            "R'", "R2", "R",
            "F'", "F2", "F",
            "B'", "B2", "B",
        };

        String[] sequence = new String[distance[combination][permutation][orientation]];
        for (int i = 0; distance[combination][permutation][orientation] > 0; i++) {
            State state = new State(
                IndexMapping.indexToCombination(combination, 4, 12),
                IndexMapping.indexToPermutation(permutation, 4),
                IndexMapping.indexToOrientation(orientation, 2, 4));

            for (int j = 0; j < N_MOVES; j++) {
                State result = state.multiply(moves[j]);

                int nextCombination = IndexMapping.combinationToIndex(result.combination, 4);
                int nextPermutation = IndexMapping.permutationToIndex(result.permutation);
                int nextOrientation = IndexMapping.orientationToIndex(result.orientation, 2);

                if (distance[nextCombination][nextPermutation][nextOrientation] == distance[combination][permutation][orientation] - 1) {
                    sequence[sequence.length - 1 - i] = inverseMoveNames[j];

                    combination = nextCombination;
                    permutation = nextPermutation;
                    orientation = nextOrientation;
                    break;
                }
            }
        }

        return sequence;
    }
}

package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;

public class RubiksCubeCrossSolver {
    public static class Move {
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

        public static HashMap<String, Move> moves;

        static {
            Move moveU = new Move(new byte[] { 1, 2, 3, 0, 4, 5, 6, 7, 8, 9, 10, 11 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
            Move moveD = new Move(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 11, 8, 9, 10 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
            Move moveL = new Move(new byte[] { 0, 1, 2, 7, 3, 5, 6, 11, 8, 9, 10, 4 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
            Move moveR = new Move(new byte[] { 0, 5, 2, 3, 4, 9, 1, 7, 8, 6, 10, 11 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
            Move moveF = new Move(new byte[] { 0, 1, 6, 3, 4, 5, 10, 2, 8, 9, 7, 11 }, new byte[] { 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0 });
            Move moveB = new Move(new byte[] { 4, 1, 2, 3, 8, 0, 6, 7, 5, 9, 10, 11 }, new byte[] { 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0 });

            moves = new HashMap<String, Move>();
            moves.put("U",  moveU);
            moves.put("U2", moveU.multiply(moveU));
            moves.put("U'", moveU.multiply(moveU).multiply(moveU));
            moves.put("D",  moveD);
            moves.put("D2", moveD.multiply(moveD));
            moves.put("D'", moveD.multiply(moveD).multiply(moveD));
            moves.put("L",  moveL);
            moves.put("L2", moveL.multiply(moveL));
            moves.put("L'", moveL.multiply(moveL).multiply(moveL));
            moves.put("R",  moveR);
            moves.put("R2", moveR.multiply(moveR));
            moves.put("R'", moveR.multiply(moveR).multiply(moveR));
            moves.put("F",  moveF);
            moves.put("F2", moveF.multiply(moveF));
            moves.put("F'", moveF.multiply(moveF).multiply(moveF));
            moves.put("B",  moveB);
            moves.put("B2", moveB.multiply(moveB));
            moves.put("B'", moveB.multiply(moveB).multiply(moveB));
        }
    }

    public static class State {
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

        public State applySequence(String[] sequence) {
            State state = this;
            for (String move : sequence) {
                state = state.multiply(Move.moves.get(move));
            }

            return state;
        }

        public static State id;

        static {
            id = new State(
                IndexMapping.indexToCombination(0, 4, 12),
                IndexMapping.indexToPermutation(0, 4),
                IndexMapping.indexToOrientation(0, 2, 4));
        }
    }

    // constants
    public static final int N_MOVES = 18;
    public static final int N_COMBINATIONS = 495;
    public static final int N_PERMUTATIONS = 24;
    public static final int N_ORIENTATIONS = 16;

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

        Move[] moves = {
            Move.moves.get("U"), Move.moves.get("U2"), Move.moves.get("U'"),
            Move.moves.get("D"), Move.moves.get("D2"), Move.moves.get("D'"),
            Move.moves.get("L"), Move.moves.get("L2"), Move.moves.get("L'"),
            Move.moves.get("R"), Move.moves.get("R2"), Move.moves.get("R'"),
            Move.moves.get("F"), Move.moves.get("F2"), Move.moves.get("F'"),
            Move.moves.get("B"), Move.moves.get("B2"), Move.moves.get("B'"),
        };

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

    public static String[][] solve(State state) {
        int combination = IndexMapping.combinationToIndex(state.combination, 4);
        int permutation = IndexMapping.permutationToIndex(state.permutation);
        int orientation = IndexMapping.orientationToIndex(state.orientation, 2);

        if (distance[combination][permutation][orientation] == 0) {
            return new String[][] { new String[0] };
        }

        ArrayList<String[]> solutions = new ArrayList<String[]>();
        for (String moveName : Move.moves.keySet()) {
            State result = state.multiply(Move.moves.get(moveName));

            int resultCombination = IndexMapping.combinationToIndex(result.combination, 4);
            int resultPermutation = IndexMapping.permutationToIndex(result.permutation);
            int resultOrientation = IndexMapping.orientationToIndex(result.orientation, 2);

            if (distance[resultCombination][resultPermutation][resultOrientation] == distance[combination][permutation][orientation] - 1) {
                for (String[] solution : solve(result)) {
                    String[] sequence = new String[solution.length + 1];
                    sequence[0] = moveName;
                    for (int i = 1; i < sequence.length; i++) {
                        sequence[i] = solution[i - 1];
                    }
                    solutions.add(sequence);
                }
            }
        }

        String[][] solutionsArray = new String[solutions.size()][];
        solutions.toArray(solutionsArray);
        return solutionsArray;
    }

    public static String[] generate(State state) {
        // pick a solution
        String[] solution = solve(state)[0];

        // invert it
        HashMap<String, String> inverseMoveNames = new HashMap<String, String>();
        inverseMoveNames.put("U",  "U'");
        inverseMoveNames.put("U2", "U2");
        inverseMoveNames.put("U'", "U");
        inverseMoveNames.put("D",  "D'");
        inverseMoveNames.put("D2", "D2");
        inverseMoveNames.put("D'", "D");
        inverseMoveNames.put("L",  "L'");
        inverseMoveNames.put("L2", "L2");
        inverseMoveNames.put("L'", "L");
        inverseMoveNames.put("R",  "R'");
        inverseMoveNames.put("R2", "R2");
        inverseMoveNames.put("R'", "R");
        inverseMoveNames.put("F",  "F'");
        inverseMoveNames.put("F2", "F2");
        inverseMoveNames.put("F'", "F");
        inverseMoveNames.put("B",  "B'");
        inverseMoveNames.put("B2", "B2");
        inverseMoveNames.put("B'", "B");

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoveNames.get(solution[solution.length - 1 - i]);
        }

        return sequence;
    }
}

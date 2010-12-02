package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RubiksPocketCubeSolver {
    public static class State {
        public byte[] permutation;
        public byte[] orientation;

        public State(byte[] permutation, byte[] orientation) {
            this.orientation = orientation;
            this.permutation = permutation;
        }

        public State multiply(State move) {
            byte[] resultPermutation = new byte[8];
            byte[] resultOrientation = new byte[8];

            for (int i = 0; i < 8; i++) {
                resultPermutation[i] = this.permutation[move.permutation[i]];
                resultOrientation[i] = (byte) ((this.orientation[move.permutation[i]] + move.orientation[i]) % 3);
            }

            return new State(resultPermutation, resultOrientation);
        }

        public static State id;

        static {
            id = new State(
                new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 },
                new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        }
    }

    private final int N_PERMUTATIONS = 40320;
    private final int N_ORIENTATIONS = 2187;

    private int minScrambleLength;
    private ArrayList<State> moves;
    private ArrayList<String> moveNames;
    private boolean initialized;
    private int[][] permutationMove;
    private int[][] orientationMove;
    private byte[] permutationDistance;
    private byte[] orientationDistance;

    public RubiksPocketCubeSolver(int minScrambleLenght, String[] generatingSet) {
        this.minScrambleLength = minScrambleLenght;

        HashMap<String, State> table = new HashMap<String, State>();
        table.put("U", new State(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }));
        table.put("D", new State(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }));
        table.put("L", new State(new byte[] { 4, 1, 2, 0, 7, 5, 6, 3 }, new byte[] { 2, 0, 0, 1, 1, 0, 0, 2 }));
        table.put("R", new State(new byte[] { 0, 2, 6, 3, 4, 1, 5, 7 }, new byte[] { 0, 1, 2, 0, 0, 2, 1, 0 }));
        table.put("F", new State(new byte[] { 0, 1, 3, 7, 4, 5, 2, 6 }, new byte[] { 0, 0, 1, 2, 0, 0, 2, 1 }));
        table.put("B", new State(new byte[] { 1, 5, 2, 3, 0, 4, 6, 7 }, new byte[] { 1, 2, 0, 0, 2, 1, 0, 0 }));

        this.moves = new ArrayList<State>();
        this.moveNames = new ArrayList<String>();
        for (String moveName : generatingSet) {
            State move = table.get(moveName);

            this.moves.add(move);
            this.moveNames.add(moveName);

            this.moves.add(move.multiply(move));
            this.moveNames.add(moveName + "2");

            this.moves.add(move.multiply(move).multiply(move));
            this.moveNames.add(moveName + "'");
        }

        this.initialized = false;
    }

    private void initialize() {
        // move tables
        this.permutationMove = new int[this.N_PERMUTATIONS][this.moves.size()];
        for (int i = 0; i < this.permutationMove.length; i++) {
            State state = new State(IndexMapping.indexToPermutation(i, 8), new byte[8]);
            for (int j = 0; j < this.moves.size(); j++) {
                this.permutationMove[i][j] =
                    IndexMapping.permutationToIndex(
                        state.multiply(this.moves.get(j)).permutation);
            }
        }

        this.orientationMove = new int[this.N_ORIENTATIONS][this.moves.size()];
        for (int i = 0; i < this.orientationMove.length; i++) {
            State state = new State(new byte[8], IndexMapping.indexToZeroSumOrientation(i, 3, 8));
            for (int j = 0; j < this.moves.size(); j++) {
                this.orientationMove[i][j] =
                    IndexMapping.zeroSumOrientationToIndex(
                        state.multiply(this.moves.get(j)).orientation, 3);
            }
        }

        // prune tables
        this.permutationDistance = new byte[this.N_PERMUTATIONS];
        for (int i = 0; i < this.permutationDistance.length; i++) {
            this.permutationDistance[i] = -1;
        }
        this.permutationDistance[0] = 0;

        int nVisited;
        int depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.permutationDistance.length; i++) {
                if (this.permutationDistance[i] == depth) {
                    for (int j = 0; j < this.moves.size(); j++) {
                        int next = this.permutationMove[i][j];
                        if (this.permutationDistance[next] < 0) {
                            this.permutationDistance[next] = (byte) (depth + 1);
                            nVisited++;
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.orientationDistance = new byte[this.N_ORIENTATIONS];
        for (int i = 0; i < this.orientationDistance.length; i++) {
            this.orientationDistance[i] = -1;
        }
        this.orientationDistance[0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.orientationDistance.length; i++) {
                if (this.orientationDistance[i] == depth) {
                    for (int j = 0; j < this.moves.size(); j++) {
                        int next = this.orientationMove[i][j];
                        if (this.orientationDistance[next] < 0) {
                            this.orientationDistance[next] = (byte) (depth + 1);
                            nVisited++;
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

        int permutation =
            IndexMapping.permutationToIndex(state.permutation);
        int orientation =
            IndexMapping.zeroSumOrientationToIndex(state.orientation, 3);

        for (int depth = this.minScrambleLength;; depth++) {
            ArrayList<String> solution = new ArrayList<String>();
            if (search(permutation, orientation, depth, solution, -1)) {
                String[] sequence = new String[solution.size()];
                solution.toArray(sequence);

                return sequence;
            }
        }
    }

    private boolean search(int permutation, int orientation, int depth, ArrayList<String> solution, int lastFace) {
        if (depth == 0) {
            return permutation == 0 && orientation == 0;
        }

        if (this.permutationDistance[permutation] <= depth &&
            this.orientationDistance[orientation] <= depth) {
            for (int i = 0; i < this.moves.size(); i++) {
                if (i / 3 == lastFace) {
                    continue;
                }

                solution.add(this.moveNames.get(i));
                if (search(
                    this.permutationMove[permutation][i],
                    this.orientationMove[orientation][i],
                    depth - 1,
                    solution,
                    i / 3)) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }

        return false;
    }

    public String[] generate(State state) {
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

        String[] solution = solve(state);

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoveNames.get(solution[solution.length - 1 - i]);
        }

        return sequence;
    }

    public State getRandomState(Random random) {
        State state = State.id;
        for (int i = 0; i < 100; i++) {
            state = state.multiply(this.moves.get(random.nextInt(this.moves.size())));
        }

        return state;
    }
}

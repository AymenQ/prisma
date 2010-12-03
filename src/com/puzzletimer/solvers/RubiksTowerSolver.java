package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RubiksTowerSolver {
    public static class State {
        public byte[] orientation;
        public byte[] edgesPermutation;
        public byte[] cornersPermutation;

        public State(byte[] orientation, byte[] edgesPermutation, byte[] cornersPermutation) {
            this.orientation = orientation;
            this.edgesPermutation = edgesPermutation;
            this.cornersPermutation = cornersPermutation;
        }

        public State multiply(State move) {
            byte[] orientation = new byte[8];
            byte[] edgesPermutation = new byte[8];
            byte[] cornersPermutation = new byte[8];

            for (int i = 0; i < 8; i++) {
                orientation[i] = (byte) ((this.orientation[move.edgesPermutation[i]] + move.orientation[i]) % 3);
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
                cornersPermutation[i] = this.cornersPermutation[move.cornersPermutation[i]];
            }

            return new State(orientation, edgesPermutation, cornersPermutation);
        }
    }

    private final int N_ORIENTATIONS = 2187;
    private final int N_EDGES_PERMUTATIONS = 40320;
    private final int N_EDGES_COMBINATIONS = 70;
    private final int N_CORNERS_PERMUTATIONS = 40320;
    private final int N_CORNERS_COMBINATIONS = 70;

    private boolean initialized;

    private State[] moves1;
    private String[] moveNames1;
    private int[] faces1;
    private State[] moves2;
    private String[] moveNames2;
    private int[] faces2;
    private int[][] orientationMove;
    private int[][] edgesPermutationMove;
    private int[][] edgesCombinationMove;
    private int[][] cornersPermutationMove;
    private int[][] cornersCombinationMove;
    private byte[] orientationDistance;
    private byte[][] edgesPermutationDistance;
    private byte[][] cornersPermutationDistance;

    public RubiksTowerSolver() {
        this.initialized = false;
    }

    private void initialize() {
        // moves
        State moveUw = new State(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 });
        State moveDw = new State(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 });
        State moveLw = new State(new byte[] { 2, 0, 0, 1, 1, 0, 0, 2 }, new byte[] { 4, 1, 2, 0, 7, 5, 6, 3 }, new byte[] { 4, 1, 2, 0, 7, 5, 6, 3 });
        State moveRw = new State(new byte[] { 0, 1, 2, 0, 0, 2, 1, 0 }, new byte[] { 0, 2, 6, 3, 4, 1, 5, 7 }, new byte[] { 0, 2, 6, 3, 4, 1, 5, 7 });
        State moveFw = new State(new byte[] { 0, 0, 1, 2, 0, 0, 2, 1 }, new byte[] { 0, 1, 3, 7, 4, 5, 2, 6 }, new byte[] { 0, 1, 3, 7, 4, 5, 2, 6 });
        State moveBw = new State(new byte[] { 1, 2, 0, 0, 2, 1, 0, 0 }, new byte[] { 1, 5, 2, 3, 0, 4, 6, 7 }, new byte[] { 1, 5, 2, 3, 0, 4, 6, 7 });
        State moveU  = new State(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 }, new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 });
        State moveD  = new State(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 }, new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 });

        this.moves1 = new State[] {
            moveUw,
            moveUw.multiply(moveUw),
            moveUw.multiply(moveUw).multiply(moveUw),
            moveDw,
            moveDw.multiply(moveDw),
            moveDw.multiply(moveDw).multiply(moveDw),
            moveLw,
            moveLw.multiply(moveLw),
            moveLw.multiply(moveLw).multiply(moveLw),
            moveRw,
            moveRw.multiply(moveRw),
            moveRw.multiply(moveRw).multiply(moveRw),
            moveFw,
            moveFw.multiply(moveFw),
            moveFw.multiply(moveFw).multiply(moveFw),
            moveBw,
            moveBw.multiply(moveBw),
            moveBw.multiply(moveBw).multiply(moveBw),
        };

        this.moveNames1 = new String[] {
            "Uw", "Uw2", "Uw'",
            "Dw", "Dw2", "Dw'",
            "Lw", "Lw2", "Lw'",
            "Rw", "Rw2", "Rw'",
            "Fw", "Fw2", "Fw'",
            "Bw", "Bw2", "Bw'",
        };

        this.faces1 = new int[] {
            0, 0, 0,
            1, 1, 1,
            2, 2, 2,
            3, 3, 3,
            4, 4, 4,
            5, 5, 5,
        };

        this.moves2 = new State[] {
            moveUw,
            moveUw.multiply(moveUw),
            moveUw.multiply(moveUw).multiply(moveUw),
            moveDw,
            moveDw.multiply(moveDw),
            moveDw.multiply(moveDw).multiply(moveDw),
            moveLw.multiply(moveLw),
            moveRw.multiply(moveRw),
            moveFw.multiply(moveFw),
            moveBw.multiply(moveBw),
            moveU,
            moveU.multiply(moveU),
            moveU.multiply(moveU).multiply(moveU),
            moveD,
            moveD.multiply(moveD),
            moveD.multiply(moveD).multiply(moveD),
        };

        this.moveNames2 = new String[] {
            "Uw", "Uw2", "Uw'",
            "Dw", "Dw2", "Dw'",
            "Lw2",
            "Rw2",
            "Fw2",
            "Bw2",
            "U",  "U2",  "U'",
            "D",  "D2",  "D'",
        };

        this.faces2 = new int[] {
            0, 0, 0,
            1, 1, 1,
            2,
            3,
            4,
            5,
            6, 6, 6,
            7, 7, 7,
        };

        // move tables
        this.orientationMove = new int[this.N_ORIENTATIONS][this.moves1.length];
        for (int i = 0; i < this.orientationMove.length; i++) {
            State state = new State(IndexMapping.indexToZeroSumOrientation(i, 3, 8), new byte[8], new byte[8]);
            for (int j = 0; j < this.moves1.length; j++) {
                this.orientationMove[i][j] =
                    IndexMapping.zeroSumOrientationToIndex(
                        state.multiply(this.moves1[j]).orientation, 3);
            }
        }

        this.edgesPermutationMove = new int[this.N_EDGES_PERMUTATIONS][this.moves2.length];
        for (int i = 0; i < this.edgesPermutationMove.length; i++) {
            State state = new State(new byte[8], IndexMapping.indexToPermutation(i, 8), new byte[8]);
            for (int j = 0; j < this.moves2.length; j++) {
                this.edgesPermutationMove[i][j] =
                    IndexMapping.permutationToIndex(
                        state.multiply(this.moves2[j]).edgesPermutation);
            }
        }

        this.edgesCombinationMove = new int[this.N_EDGES_COMBINATIONS][this.moves2.length];
        for (int i = 0; i < this.edgesCombinationMove.length; i++) {
            boolean[] combination = IndexMapping.indexToCombination(i, 4, 8);

            byte[] edges = new byte[8];
            byte nextTop = 0;
            byte nextBottom = 4;

            for (int j = 0; j < edges.length; j++) {
                if (combination[j]) {
                    edges[j] = nextTop++;
                } else {
                    edges[j] = nextBottom++;
                }
            }

            State state = new State(new byte[8], edges, new byte[8]);
            for (int j = 0; j < this.moves2.length; j++) {
                State result = state.multiply(this.moves2[j]);

                boolean[] isTopEdge = new boolean[8];
                for (int k = 0; k < isTopEdge.length; k++) {
                    isTopEdge[k] = result.edgesPermutation[k] < 4;
                }

                this.edgesCombinationMove[i][j] = IndexMapping.combinationToIndex(isTopEdge, 4);
            }
        }

        this.cornersPermutationMove = new int[this.N_CORNERS_PERMUTATIONS][this.moves2.length];
        for (int i = 0; i < this.cornersPermutationMove.length; i++) {
            State state = new State(new byte[8], new byte[8], IndexMapping.indexToPermutation(i, 8));
            for (int j = 0; j < this.moves2.length; j++) {
                this.cornersPermutationMove[i][j] =
                    IndexMapping.permutationToIndex(
                        state.multiply(this.moves2[j]).cornersPermutation);
            }
        }

        this.cornersCombinationMove = new int[this.N_CORNERS_COMBINATIONS][this.moves2.length];
        for (int i = 0; i < this.cornersCombinationMove.length; i++) {
            boolean[] combination = IndexMapping.indexToCombination(i, 4, 8);

            byte[] corners = new byte[8];
            byte nextTop = 0;
            byte nextBottom = 4;

            for (int j = 0; j < corners.length; j++) {
                if (combination[j]) {
                    corners[j] = nextTop++;
                } else {
                    corners[j] = nextBottom++;
                }
            }

            State state = new State(new byte[8], new byte[8], corners);
            for (int j = 0; j < this.moves2.length; j++) {
                State result = state.multiply(this.moves2[j]);

                boolean[] isTopCorner = new boolean[8];
                for (int k = 0; k < isTopCorner.length; k++) {
                    isTopCorner[k] = result.cornersPermutation[k] < 4;
                }

                this.cornersCombinationMove[i][j] = IndexMapping.combinationToIndex(isTopCorner, 4);
            }
        }

        // prune tables
        this.orientationDistance = new byte[this.N_ORIENTATIONS];
        for (int i = 0; i < this.orientationDistance.length; i++) {
            this.orientationDistance[i] = -1;
        }
        this.orientationDistance[0] = 0;

        int depth = 0;
        int nVisited;
        do {
            nVisited = 0;

            for (int i = 0; i < this.orientationDistance.length; i++) {
                if (this.orientationDistance[i] == depth) {
                    for (int j = 0; j < this.moves1.length; j++) {
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

        this.edgesPermutationDistance = new byte[this.N_EDGES_PERMUTATIONS][this.N_CORNERS_COMBINATIONS];
        for (int i = 0; i < this.edgesPermutationDistance.length; i++) {
            for (int j = 0; j < this.edgesPermutationDistance[i].length; j++) {
                this.edgesPermutationDistance[i][j] = -1;
            }
        }
        this.edgesPermutationDistance[0][0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.edgesPermutationDistance.length; i++) {
                for (int j = 0; j < this.edgesPermutationDistance[i].length; j++) {
                    if (this.edgesPermutationDistance[i][j] == depth) {
                        for (int k = 0; k < this.moves2.length; k++) {
                            int nextPermutation = this.edgesPermutationMove[i][k];
                            int nextCombination = this.cornersCombinationMove[j][k];
                            if (this.edgesPermutationDistance[nextPermutation][nextCombination] < 0) {
                                this.edgesPermutationDistance[nextPermutation][nextCombination] = (byte) (depth + 1);
                                nVisited++;
                            }
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.cornersPermutationDistance = new byte[this.N_CORNERS_PERMUTATIONS][this.N_EDGES_COMBINATIONS];
        for (int i = 0; i < this.cornersPermutationDistance.length; i++) {
            for (int j = 0; j < this.cornersPermutationDistance[i].length; j++) {
                this.cornersPermutationDistance[i][j] = -1;
            }
        }
        this.cornersPermutationDistance[0][0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.cornersPermutationDistance.length; i++) {
                for (int j = 0; j < this.cornersPermutationDistance[i].length; j++) {
                    if (this.cornersPermutationDistance[i][j] == depth) {
                        for (int k = 0; k < this.moves2.length; k++) {
                            int nextPermutation = this.cornersPermutationMove[i][k];
                            int nextCombination = this.edgesCombinationMove[j][k];
                            if (this.cornersPermutationDistance[nextPermutation][nextCombination] < 0) {
                                this.cornersPermutationDistance[nextPermutation][nextCombination] = (byte) (depth + 1);
                                nVisited++;
                            }
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

        // orientation
        int orientation =
            IndexMapping.zeroSumOrientationToIndex(state.orientation, 3);

        for (int depth = 0; ; depth++) {
            ArrayList<Integer> solution = new ArrayList<Integer>();
            if (search(orientation, depth, solution, -1)) {
                ArrayList<String> sequence = new ArrayList<String>();

                State state2 = state;
                for (int moveIndex : solution) {
                    sequence.add(this.moveNames1[moveIndex]);
                    state2 = state2.multiply(this.moves1[moveIndex]);
                }

                String[] solution2 = solve2(state2);
                for (String move : solution2) {
                    sequence.add(move);
                }

                String[] sequenceArray = new String[sequence.size()];
                sequence.toArray(sequenceArray);

                return sequenceArray;
            }
        }
    }

    private boolean search(int orientation, int depth, ArrayList<Integer> solution, int lastFace) {
        if (depth == 0) {
            return orientation == 0;
        }

        if (this.orientationDistance[orientation] <= depth) {
            for (int i = 0; i < this.moves1.length; i++) {
                if (this.faces1[i] == lastFace) {
                    continue;
                }

                solution.add(i);
                if (search(
                    this.orientationMove[orientation][i],
                    depth - 1,
                    solution,
                    this.faces1[i])) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }

        return false;
    }

    private String[] solve2(State state) {
        // edges permutation
        int edgesPermutation =
            IndexMapping.permutationToIndex(state.edgesPermutation);

        // edges combination
        boolean[] isTopEdge = new boolean[8];
        for (int k = 0; k < isTopEdge.length; k++) {
            isTopEdge[k] = state.edgesPermutation[k] < 4;
        }
        int edgesCombination = IndexMapping.combinationToIndex(isTopEdge, 4);

        // corners permutation
        int cornersPermutation =
            IndexMapping.permutationToIndex(state.cornersPermutation);

        // corners combination
        boolean[] isTopCorner = new boolean[8];
        for (int k = 0; k < isTopCorner.length; k++) {
            isTopCorner[k] = state.cornersPermutation[k] < 4;
        }
        int cornersCombination = IndexMapping.combinationToIndex(isTopCorner, 4);

        for (int depth = 0; ; depth++) {
            ArrayList<Integer> solution = new ArrayList<Integer>();
            if (search2(
                edgesPermutation,
                edgesCombination,
                cornersPermutation,
                cornersCombination,
                depth,
                solution,
                -1)) {
                String[] sequence = new String[solution.size()];
                for (int i = 0; i < solution.size(); i++) {
                    sequence[i] = this.moveNames2[solution.get(i)];
                }

                return sequence;
            }
        }
    }

    private boolean search2(int edgesPermutation, int edgesCombination, int cornersPermutation, int cornersCombination, int depth, ArrayList<Integer> solution, int lastFace) {
        if (depth == 0) {
            return edgesPermutation == 0 && cornersPermutation == 0;
        }

        if (this.edgesPermutationDistance[edgesPermutation][cornersCombination] <= depth &&
            this.cornersPermutationDistance[cornersPermutation][edgesCombination] <= depth) {
            for (int i = 0; i < this.moves2.length; i++) {
                if (this.faces2[i] == lastFace) {
                    continue;
                }

                solution.add(i);
                if (search2(
                    this.edgesPermutationMove[edgesPermutation][i],
                    this.edgesCombinationMove[edgesCombination][i],
                    this.cornersPermutationMove[cornersPermutation][i],
                    this.cornersCombinationMove[cornersCombination][i],
                    depth - 1,
                    solution,
                    this.faces2[i])) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }

        return false;
    }

    public String[] generate(State state) {
        String[] solution = solve(state);

        HashMap<String, String> inverseMoveNames = new HashMap<String, String>();
        inverseMoveNames.put("Uw",  "Uw'");
        inverseMoveNames.put("Uw2", "Uw2");
        inverseMoveNames.put("Uw'", "Uw");
        inverseMoveNames.put("Dw",  "Dw'");
        inverseMoveNames.put("Dw2", "Dw2");
        inverseMoveNames.put("Dw'", "Dw");
        inverseMoveNames.put("Lw",  "Lw'");
        inverseMoveNames.put("Lw2", "Lw2");
        inverseMoveNames.put("Lw'", "Lw");
        inverseMoveNames.put("Rw",  "Rw'");
        inverseMoveNames.put("Rw2", "Rw2");
        inverseMoveNames.put("Rw'", "Rw");
        inverseMoveNames.put("Fw",  "Fw'");
        inverseMoveNames.put("Fw2", "Fw2");
        inverseMoveNames.put("Fw'", "Fw");
        inverseMoveNames.put("Bw",  "Bw'");
        inverseMoveNames.put("Bw2", "Bw2");
        inverseMoveNames.put("Bw'", "Bw");
        inverseMoveNames.put("U",  "U'");
        inverseMoveNames.put("U2", "U2");
        inverseMoveNames.put("U'", "U");
        inverseMoveNames.put("D",  "D'");
        inverseMoveNames.put("D2", "D2");
        inverseMoveNames.put("D'", "D");

        String[] sequence = new String[solution.length];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = inverseMoveNames.get(solution[solution.length - 1 - i]);
        }

        return sequence;
    }

    public State getRandomState(Random random) {
        byte[] orientation =
            IndexMapping.indexToZeroSumOrientation(
                random.nextInt(this.N_ORIENTATIONS), 3, 8);
        byte[] edgesPermutation =
            IndexMapping.indexToPermutation(
                random.nextInt(this.N_EDGES_PERMUTATIONS), 8);
        byte[] cornersPermutation =
            IndexMapping.indexToPermutation(
                random.nextInt(this.N_CORNERS_PERMUTATIONS), 8);

        return new State(orientation, edgesPermutation, cornersPermutation);
    }
}

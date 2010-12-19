package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PyraminxSolver {
    public static class State {
        public byte[] tipsOrientation;
        public byte[] verticesOrientation;
        public byte[] edgesPermutation;
        public byte[] edgesOrientation;

        public State(
                byte[] tipsOrientation,
                byte[] verticesOrientation,
                byte[] edgesPermutation,
                byte[] edgesOrientation) {
            this.tipsOrientation = tipsOrientation;
            this.verticesOrientation = verticesOrientation;
            this.edgesPermutation = edgesPermutation;
            this.edgesOrientation = edgesOrientation;
        }

        public State multiply(State move) {
            byte[] tipsOrientation = new byte[4];
            for (int i = 0; i < 4; i++) {
                tipsOrientation[i] = (byte) ((this.tipsOrientation[i] + move.tipsOrientation[i]) % 3);
            }

            byte[] verticesOrientation = new byte[4];
            for (int i = 0; i < 4; i++) {
                verticesOrientation[i] = (byte) ((this.verticesOrientation[i] + move.verticesOrientation[i]) % 3);
            }

            byte[] edgesPermutation = new byte[6];
            byte[] edgesOrientation = new byte[6];
            for (int i = 0; i < 6; i++) {
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
                edgesOrientation[i] = (byte) ((this.edgesOrientation[move.edgesPermutation[i]] + move.edgesOrientation[i]) % 2);
            }

            return new State(tipsOrientation, verticesOrientation, edgesPermutation, edgesOrientation);
        }
    }

    public final int N_TIPS_ORIENTATIONS = 81;
    public final int N_VERTICES_ORIENTATIONS = 81;
    public final int N_EDGES_PERMUTATIONS = 360;
    public final int N_EDGES_ORIENTATIONS = 32;

    private int minScrambleLength;
    private boolean initialized;
    private State[] tipMoves;
    private String[] tipMoveNames;
    private State[] moves;
    private String[] moveNames;
    private int[][] tipsOrientationMove;
    private int[][] verticesOrientationMove;
    private int[][] edgesPermutationMove;
    private int[][] edgesOrientationMove;
    private byte[] tipsOrientationDistance;
    private byte[] verticesOrientationDistance;
    private byte[] edgesPermutationDistance;
    private byte[] edgesOrientationDistance;

    public PyraminxSolver(int minScrambleLength) {
        this.minScrambleLength = minScrambleLength;
        this.initialized = false;
    }

    private void initialize() {
        State moveu = new State(new byte[] { 1, 0, 0, 0 }, new byte[] { 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0 });
        State movel = new State(new byte[] { 0, 1, 0, 0 }, new byte[] { 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0 });
        State mover = new State(new byte[] { 0, 0, 1, 0 }, new byte[] { 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0 });
        State moveb = new State(new byte[] { 0, 0, 0, 1 }, new byte[] { 0, 0, 0, 0 }, new byte[] { 0, 1, 2, 3, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0 });

        this.tipMoves = new State[] {
            moveu,
            moveu.multiply(moveu),
            movel,
            movel.multiply(movel),
            mover,
            mover.multiply(mover),
            moveb,
            moveb.multiply(moveb),
        };

        this.tipMoveNames = new String[] {
            "u", "u'",
            "l", "l'",
            "r", "r'",
            "b", "b'",
        };

        State moveU = new State(new byte[] { 1, 0, 0, 0 }, new byte[] { 1, 0, 0, 0 }, new byte[] { 2, 0, 1, 3, 4, 5 }, new byte[] { 0, 0, 0, 0, 0, 0 });
        State moveL = new State(new byte[] { 0, 1, 0, 0 }, new byte[] { 0, 1, 0, 0 }, new byte[] { 0, 1, 5, 3, 2, 4 }, new byte[] { 0, 0, 1, 0, 0, 1 });
        State moveR = new State(new byte[] { 0, 0, 1, 0 }, new byte[] { 0, 0, 1, 0 }, new byte[] { 0, 4, 2, 1, 3, 5 }, new byte[] { 0, 1, 0, 0, 1, 0 });
        State moveB = new State(new byte[] { 0, 0, 0, 1 }, new byte[] { 0, 0, 0, 1 }, new byte[] { 3, 1, 2, 5, 4, 0 }, new byte[] { 1, 0, 0, 1, 0, 0 });

        this.moves = new State[] {
            moveU,
            moveU.multiply(moveU),
            moveL,
            moveL.multiply(moveL),
            moveR,
            moveR.multiply(moveR),
            moveB,
            moveB.multiply(moveB),
        };

        this.moveNames = new String[] {
            "U", "U'",
            "L", "L'",
            "R", "R'",
            "B", "B'",
        };

        // move tables
        this.tipsOrientationMove = new int[this.N_TIPS_ORIENTATIONS][this.tipMoveNames.length];
        for (int i = 0; i < this.tipsOrientationMove.length; i++) {
            State state = new State(IndexMapping.indexToOrientation(i, 3, 4), new byte[4], new byte[6], new byte[6]);
            for (int j = 0; j < this.moves.length; j++) {
                this.tipsOrientationMove[i][j] = IndexMapping.orientationToIndex(state.multiply(this.tipMoves[j]).tipsOrientation, 3);
            }
        }

        this.verticesOrientationMove = new int[this.N_VERTICES_ORIENTATIONS][this.moves.length];
        for (int i = 0; i < this.verticesOrientationMove.length; i++) {
            State state = new State(new byte[4], IndexMapping.indexToOrientation(i, 3, 4), new byte[6], new byte[6]);
            for (int j = 0; j < this.moves.length; j++) {
                this.verticesOrientationMove[i][j] = IndexMapping.orientationToIndex(state.multiply(this.moves[j]).verticesOrientation, 3);
            }
        }

        this.edgesPermutationMove = new int[this.N_EDGES_PERMUTATIONS][this.moves.length];
        for (int i = 0; i < this.edgesPermutationMove.length; i++) {
            State state = new State(new byte[4], new byte[4], IndexMapping.indexToEvenPermutation(i, 6), new byte[6]);
            for (int j = 0; j < this.moves.length; j++) {
                this.edgesPermutationMove[i][j] = IndexMapping.evenPermutationToIndex(state.multiply(this.moves[j]).edgesPermutation);
            }
        }

        this.edgesOrientationMove = new int[this.N_EDGES_ORIENTATIONS][this.moves.length];
        for (int i = 0; i < this.edgesOrientationMove.length; i++) {
            State state = new State(new byte[4], new byte[4], new byte[6], IndexMapping.indexToZeroSumOrientation(i, 2, 6));
            for (int j = 0; j < this.moves.length; j++) {
                this.edgesOrientationMove[i][j] = IndexMapping.zeroSumOrientationToIndex(state.multiply(this.moves[j]).edgesOrientation, 2);
            }
        }

        // prune tables
        this.tipsOrientationDistance = new byte[this.N_TIPS_ORIENTATIONS];
        for (int i = 0; i < this.tipsOrientationDistance.length; i++) {
            this.tipsOrientationDistance[i] = -1;
        }
        this.tipsOrientationDistance[0] = 0;

        int depth = 0;
        int nVisited;
        do {
            nVisited = 0;

            for (int i = 0; i < this.tipsOrientationDistance.length; i++) {
                if (this.tipsOrientationDistance[i] == depth) {
                    for (int k = 0; k < this.tipMoves.length; k++) {
                        int next = this.tipsOrientationMove[i][k];
                        if (this.tipsOrientationDistance[next] < 0) {
                            this.tipsOrientationDistance[next] = (byte) (depth + 1);
                            nVisited++;
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.verticesOrientationDistance = new byte[this.N_VERTICES_ORIENTATIONS];
        for (int i = 0; i < this.verticesOrientationDistance.length; i++) {
            this.verticesOrientationDistance[i] = -1;
        }
        this.verticesOrientationDistance[0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.verticesOrientationDistance.length; i++) {
                if (this.verticesOrientationDistance[i] == depth) {
                    for (int k = 0; k < this.moves.length; k++) {
                        int next = this.verticesOrientationMove[i][k];
                        if (this.verticesOrientationDistance[next] < 0) {
                            this.verticesOrientationDistance[next] = (byte) (depth + 1);
                            nVisited++;
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.edgesPermutationDistance = new byte[this.N_EDGES_PERMUTATIONS];
        for (int i = 0; i < this.edgesPermutationDistance.length; i++) {
            this.edgesPermutationDistance[i] = -1;
        }
        this.edgesPermutationDistance[0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.edgesPermutationDistance.length; i++) {
                if (this.edgesPermutationDistance[i] == depth) {
                    for (int k = 0; k < this.moves.length; k++) {
                        int next = this.edgesPermutationMove[i][k];
                        if (this.edgesPermutationDistance[next] < 0) {
                            this.edgesPermutationDistance[next] = (byte) (depth + 1);
                            nVisited++;
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.edgesOrientationDistance = new byte[this.N_EDGES_ORIENTATIONS];
        for (int i = 0; i < this.edgesOrientationDistance.length; i++) {
            this.edgesOrientationDistance[i] = -1;
        }
        this.edgesOrientationDistance[0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.edgesOrientationDistance.length; i++) {
                if (this.edgesOrientationDistance[i] == depth) {
                    for (int k = 0; k < this.moves.length; k++) {
                        int next = this.edgesOrientationMove[i][k];
                        if (this.edgesOrientationDistance[next] < 0) {
                            this.edgesOrientationDistance[next] = (byte) (depth + 1);
                            nVisited++;
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.initialized = true;
    }

    private String[] solveTips(State state) {
        if (!this.initialized) {
            initialize();
        }

        int tipsOrientation =
            IndexMapping.orientationToIndex(state.tipsOrientation, 3);

        for (int depth = 0; ; depth++) {
            ArrayList<String> solution = new ArrayList<String>();
            if (searchTips(tipsOrientation, depth, solution, -1)) {
                String[] sequence = new String[solution.size()];
                solution.toArray(sequence);

                return sequence;
            }
        }
    }

    private boolean searchTips(int tipsOrientation, int depth, ArrayList<String> solution, int lastVertex) {
        if (depth == 0) {
            return tipsOrientation == 0;
        }

        if (this.tipsOrientationDistance[tipsOrientation] <= depth) {
            for (int i = 0; i < this.tipMoves.length; i++) {
                if (i / 2 == lastVertex) {
                    continue;
                }

                solution.add(this.tipMoveNames[i]);
                if (searchTips(
                    this.tipsOrientationMove[tipsOrientation][i],
                    depth - 1,
                    solution,
                    i / 2)) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }

        return false;
    }

    private String[] solve(State state) {
        if (!this.initialized) {
            initialize();
        }

        int verticesOrientation =
            IndexMapping.orientationToIndex(state.verticesOrientation, 3);
        int edgesPermutation =
            IndexMapping.evenPermutationToIndex(state.edgesPermutation);
        int edgesOrientation =
            IndexMapping.zeroSumOrientationToIndex(state.edgesOrientation, 2);

        for (int depth = this.minScrambleLength; ; depth++) {
            ArrayList<String> solution = new ArrayList<String>();
            if (search(verticesOrientation, edgesPermutation, edgesOrientation, depth, solution, -1)) {
                String[] sequence = new String[solution.size()];
                solution.toArray(sequence);

                return sequence;
            }
        }
    }

    private boolean search(
            int verticesOrientation,
            int edgesPermutation,
            int edgesOrientation,
            int depth,
            ArrayList<String> solution,
            int lastVertex) {
        if (depth == 0) {
            return verticesOrientation == 0 &&
                   edgesPermutation == 0 &&
                   edgesOrientation == 0;
        }

        if (this.verticesOrientationDistance[verticesOrientation] <= depth &&
            this.edgesPermutationDistance[edgesPermutation] <= depth &&
            this.edgesOrientationDistance[edgesOrientation] <= depth) {
            for (int i = 0; i < this.moves.length; i++) {
                if (i / 2 == lastVertex) {
                    continue;
                }

                solution.add(this.moveNames[i]);
                if (search(
                    this.verticesOrientationMove[verticesOrientation][i],
                    this.edgesPermutationMove[edgesPermutation][i],
                    this.edgesOrientationMove[edgesOrientation][i],
                    depth - 1,
                    solution,
                    i / 2)) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }

        return false;
    }

    public String[] generate(State state) {
        HashMap<String, String> inverseMoveNames = new HashMap<String, String>();
        inverseMoveNames.put("u",  "u'");
        inverseMoveNames.put("u'", "u");
        inverseMoveNames.put("l",  "l'");
        inverseMoveNames.put("l'", "l");
        inverseMoveNames.put("r",  "r'");
        inverseMoveNames.put("r'", "r");
        inverseMoveNames.put("b",  "b'");
        inverseMoveNames.put("b'", "b");
        inverseMoveNames.put("U",  "U'");
        inverseMoveNames.put("U'", "U");
        inverseMoveNames.put("L",  "L'");
        inverseMoveNames.put("L'", "L");
        inverseMoveNames.put("R",  "R'");
        inverseMoveNames.put("R'", "R");
        inverseMoveNames.put("B",  "B'");
        inverseMoveNames.put("B'", "B");

        String[] solution = solve(state);

        HashMap<String, State> moves = new HashMap<String, State>();
        moves.put("U",  this.moves[0]);
        moves.put("U'", this.moves[1]);
        moves.put("L",  this.moves[2]);
        moves.put("L'", this.moves[3]);
        moves.put("R",  this.moves[4]);
        moves.put("R'", this.moves[5]);
        moves.put("B",  this.moves[6]);
        moves.put("B'", this.moves[7]);

        for (String move : solution) {
            state = state.multiply(moves.get(move));
        }

        String[] tipsSolution = solveTips(state);

        String[] sequence = new String[tipsSolution.length + solution.length];
        for (int i = 0; i < solution.length; i++) {
            sequence[i] = inverseMoveNames.get(solution[solution.length - 1 - i]);
        }
        for (int i = 0; i < tipsSolution.length; i++) {
            sequence[solution.length + i] = inverseMoveNames.get(tipsSolution[tipsSolution.length - 1 - i]);
        }

        return sequence;
    }

    public State getRandomState(Random random) {
        int tipsOrientation =
            random.nextInt(this.N_TIPS_ORIENTATIONS);
        int verticesOrientation =
            random.nextInt(this.N_VERTICES_ORIENTATIONS);
        int edgesPermutation =
            random.nextInt(this.N_EDGES_PERMUTATIONS);
        int edgesOrientation =
            random.nextInt(this.N_EDGES_ORIENTATIONS);

        return new State(
            IndexMapping.indexToOrientation(tipsOrientation, 3, 4),
            IndexMapping.indexToOrientation(verticesOrientation, 3, 4),
            IndexMapping.indexToEvenPermutation(edgesPermutation, 6),
            IndexMapping.indexToZeroSumOrientation(edgesOrientation, 2, 6));
    }
}

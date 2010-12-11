package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Square1Solver {
    public static class State {
        public byte[] permutation;

        public State(byte[] permutation) {
            this.permutation = permutation;
        }

        public boolean isTwistable() {
            return this.permutation[1] != this.permutation[2] &&
                   this.permutation[7] != this.permutation[8] &&
                   this.permutation[13] != this.permutation[14] &&
                   this.permutation[19] != this.permutation[20];
        }

        public State multiply(State move) {
            byte [] permutation = new byte[24];

            for (int i = 0; i < permutation.length; i++) {
                permutation[i] = this.permutation[move.permutation[i]];
            }

            return new State(permutation);
        }

        public int getShapeIndex() {
            byte[] cuts = new byte[24];
            for (int i = 0; i < cuts.length; i++) {
                cuts[i] = 0;
            }

            for (int i = 0; i < 12; i++) {
                int next = (i + 1) % 12;
                if (this.permutation[i] != this.permutation[next]) {
                    cuts[i] = 1;
                }
            }

            for (int i = 0; i < 12; i++) {
                int next = (i + 1) % 12;
                if (this.permutation[12 + i] != this.permutation[12 + next]) {
                    cuts[12 + i] = 1;
                }
            }

            return IndexMapping.orientationToIndex(cuts, 2);
        }

        public byte[] getPiecesPermutation() {
            byte[] permutation = new byte[16];
            int nextSlot = 0;

            for (int i = 0; i < 12; i++) {
                int next = (i + 1) % 12;
                if (this.permutation[i] != this.permutation[next]) {
                    permutation[nextSlot++] = this.permutation[i];
                }
            }

            for (int i = 0; i < 12; i++) {
                int next = 12 + (i + 1) % 12;
                if (this.permutation[12 + i] != this.permutation[next]) {
                    permutation[nextSlot++] = this.permutation[12 + i];
                }
            }

            return permutation;
        }

        public CubeState toCubeState() {
            int[] cornerIndices = { 0, 3, 6, 9, 12, 15, 18, 21 };

            byte[] cornersPermutation = new byte[8];
            for (int i = 0; i < cornersPermutation.length; i++) {
                cornersPermutation[i] = this.permutation[cornerIndices[i]];
            }

            int[] edgeIndices = { 1, 4, 7, 10, 13, 16, 19, 22 };

            byte[] edgesPermutation = new byte[8];
            for (int i = 0; i < edgesPermutation.length; i++) {
                edgesPermutation[i] = (byte) (this.permutation[edgeIndices[i]] - 8);
            }

            return new CubeState(cornersPermutation, edgesPermutation);
        }

        public static State id;

        static {
            id = new State(new byte[] {
                 0,  8,  1,  1,  9,  2,  2, 10,  3,  3, 11,  0,
                 4, 12,  5,  5, 13,  6,  6, 14,  7,  7, 15,  4,
            });
        }
    }

    private static class CubeState {
        public byte[] cornersPermutation;
        public byte[] edgesPermutation;

        public CubeState(byte[] cornersPermutation, byte[] edgesPermutation) {
            this.cornersPermutation = cornersPermutation;
            this.edgesPermutation = edgesPermutation;
        }

        public CubeState multiply(CubeState move) {
            byte[] cornersPermutation = new byte[8];
            byte[] edgesPermutation = new byte[8];

            for (int i = 0; i < 8; i++) {
                cornersPermutation[i] = this.cornersPermutation[move.cornersPermutation[i]];
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
            }

            return new CubeState(cornersPermutation, edgesPermutation);
        }
    }

    private boolean initialized;

    // phase 1
    private State[] moves1;
    private ArrayList<State> shapes;
    private HashMap<Integer, Integer> evenShapeDistance;
    private HashMap<Integer, Integer> oddShapeDistance;

    // phase 2
    public final int N_CORNERS_PERMUTATIONS = 40320;
    public final int N_CORNERS_COMBINATIONS = 70;
    public final int N_EDGES_PERMUTATIONS = 40320;
    public final int N_EDGES_COMBINATIONS = 70;

    private CubeState[] moves2;
    private int[][] cornersPermutationMove;
    private int[][] cornersCombinationMove;
    private int[][] edgesPermutationMove;
    private int[][] edgesCombinationMove;
    private byte[][] cornersDistance;
    private byte[][] edgesDistance;

    public Square1Solver() {
        this.initialized = false;
    }

    private void initialize() {
        // -- phase 1 --

        // moves
        this.moves1 = new State[23];

        State move10 = new State(new byte[] {
            11,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10,
            12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
        });

        State move = move10;
        for (int i = 0; i < 11; i++) {
            this.moves1[i] = move;
            move = move.multiply(move10);
        }

        State move01 = new State(new byte[] {
             0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11,
            13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 12,
        });

        move = move01;
        for (int i = 0; i < 11; i++) {
            this.moves1[11 + i] = move;
            move = move.multiply(move01);
        }

        State moveTwist = new State(new byte[] {
             0,  1, 19, 18, 17, 16, 15, 14,  8,  9, 10, 11,
            12, 13,  7,  6,  5,  4,  3,  2, 20, 21, 22, 23,
        });

        this.moves1[22] = moveTwist;

        // shape tables
        this.shapes = new ArrayList<State>();

        this.evenShapeDistance = new HashMap<Integer, Integer>();
        this.oddShapeDistance = new HashMap<Integer, Integer>();
        this.evenShapeDistance.put(State.id.getShapeIndex(), 0);

        ArrayList<State> fringe = new ArrayList<State>();
        fringe.add(State.id);

        int depth = 0;
        while (fringe.size() > 0) {
            ArrayList<State> newFringe = new ArrayList<State>();
            for (State state : fringe) {
                if (state.isTwistable()) {
                    this.shapes.add(state);
                }

                for (int i = 0; i < this.moves1.length; i++) {
                    if (i == 22 && !state.isTwistable()) {
                        continue;
                    }

                    State next = state.multiply(this.moves1[i]);

                    HashMap<Integer, Integer> distanceTable =
                        isEvenPermutation(next.getPiecesPermutation()) ?
                            this.evenShapeDistance : this.oddShapeDistance;

                    if (!distanceTable.containsKey(next.getShapeIndex())) {
                        distanceTable.put(next.getShapeIndex(), depth + 1);
                        newFringe.add(next);
                    }
                }
            }

            fringe = newFringe;
            depth++;
        }

        // -- phase 2 --

        // moves
        CubeState move30 =          new CubeState(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 });
        CubeState move03 =          new CubeState(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 });
        CubeState moveTwistTop =    new CubeState(new byte[] { 0, 6, 5, 3, 4, 2, 1, 7 }, new byte[] { 6, 5, 2, 3, 4, 1, 0, 7 });
        CubeState moveTwistBottom = new CubeState(new byte[] { 0, 6, 5, 3, 4, 2, 1, 7 }, new byte[] { 0, 5, 4, 3, 2, 1, 6, 7 });

        this.moves2 = new CubeState[] {
            move30,
            move30.multiply(move30),
            move30.multiply(move30).multiply(move30),
            move03,
            move03.multiply(move03),
            move03.multiply(move03).multiply(move03),
            moveTwistTop,
            moveTwistBottom,
        };

        // move tables
        this.cornersPermutationMove = new int[this.N_CORNERS_PERMUTATIONS][this.moves2.length];
        for (int i = 0; i < this.cornersPermutationMove.length; i++) {
            CubeState state = new CubeState(IndexMapping.indexToPermutation(i, 8), new byte[8]);
            for (int j = 0; j < this.cornersPermutationMove[i].length; j++) {
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

            CubeState state = new CubeState(corners, new byte[8]);
            for (int j = 0; j < this.cornersCombinationMove[i].length; j++) {
                CubeState result = state.multiply(this.moves2[j]);

                boolean[] isTopCorner = new boolean[8];
                for (int k = 0; k < isTopCorner.length; k++) {
                    isTopCorner[k] = result.cornersPermutation[k] < 4;
                }

                this.cornersCombinationMove[i][j] =
                    IndexMapping.combinationToIndex(isTopCorner, 4);
            }
        }

        this.edgesPermutationMove = new int[this.N_EDGES_PERMUTATIONS][this.moves2.length];
        for (int i = 0; i < this.edgesPermutationMove.length; i++) {
            CubeState state = new CubeState(new byte[8], IndexMapping.indexToPermutation(i, 8));
            for (int j = 0; j < this.edgesPermutationMove[i].length; j++) {
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

            CubeState state = new CubeState(new byte[8], edges);
            for (int j = 0; j < this.edgesCombinationMove[i].length; j++) {
                CubeState result = state.multiply(this.moves2[j]);

                boolean[] isTopEdge = new boolean[8];
                for (int k = 0; k < isTopEdge.length; k++) {
                    isTopEdge[k] = result.edgesPermutation[k] < 4;
                }

                this.edgesCombinationMove[i][j] =
                    IndexMapping.combinationToIndex(isTopEdge, 4);
            }
        }

        // prune tables
        this.cornersDistance = new byte[this.N_CORNERS_PERMUTATIONS][this.N_EDGES_COMBINATIONS];
        for (int i = 0; i < this.cornersDistance.length; i++) {
            for (int j = 0; j < this.cornersDistance[i].length; j++) {
                this.cornersDistance[i][j] = -1;
            }
        }
        this.cornersDistance[0][0] = 0;

        int nVisited;
        do {
            nVisited = 0;

            for (int i = 0; i < this.cornersDistance.length; i++) {
                for (int j = 0; j < this.cornersDistance[i].length; j++) {
                    if (this.cornersDistance[i][j] == depth) {
                        for (int k = 0; k < this.moves2.length; k++) {
                            int nextCornerPermutation = this.cornersPermutationMove[i][k];
                            int nextEdgeCombination = this.edgesCombinationMove[j][k];
                            if (this.cornersDistance[nextCornerPermutation][nextEdgeCombination] < 0) {
                                this.cornersDistance[nextCornerPermutation][nextEdgeCombination] = (byte) (depth + 1);
                                nVisited++;
                            }
                        }
                    }
                }
            }

            depth++;
        } while (nVisited > 0);

        this.edgesDistance = new byte[this.N_EDGES_PERMUTATIONS][this.N_CORNERS_COMBINATIONS];
        for (int i = 0; i < this.edgesDistance.length; i++) {
            for (int j = 0; j < this.edgesDistance[i].length; j++) {
                this.edgesDistance[i][j] = -1;
            }
        }
        this.edgesDistance[0][0] = 0;

        depth = 0;
        do {
            nVisited = 0;

            for (int i = 0; i < this.edgesDistance.length; i++) {
                for (int j = 0; j < this.edgesDistance[i].length; j++) {
                    if (this.edgesDistance[i][j] == depth) {
                        for (int k = 0; k < this.moves2.length; k++) {
                            int nextEdgesPermutation = this.edgesPermutationMove[i][k];
                            int nextCornersCombination = this.cornersCombinationMove[j][k];
                            if (this.edgesDistance[nextEdgesPermutation][nextCornersCombination] < 0) {
                                this.edgesDistance[nextEdgesPermutation][nextCornersCombination] = (byte) (depth + 1);
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

    private boolean isEvenPermutation(byte[] permutation) {
        int nInversions = 0;
        for (int i = 0; i < permutation.length; i++) {
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[i] > permutation[j]) {
                    nInversions++;
                }
            }
        }

        return nInversions % 2 == 0;
    }

    public String[] solve(State state) {
        ArrayList<String> sequence = new ArrayList<String>();

        int top = 0;
        int bottom = 0;
        for (int moveIndex : solution(state)) {
            if (moveIndex < 11) {
                top += moveIndex + 1;
                top %= 12;
            } else if (moveIndex < 22) {
                bottom += (moveIndex - 11) + 1;
                bottom %= 12;
            } else {
                if (top != 0 || bottom != 0) {
                    if (top > 6) {
                        top = -(12 - top);
                    }

                    if (bottom > 6) {
                        bottom = -(12 - bottom);
                    }

                    sequence.add(String.format("(%d,%d)", top, bottom));
                    top = 0;
                    bottom = 0;
                }

                sequence.add("/");
            }
        }

        if (top != 0 || bottom != 0) {
            if (top > 6) {
                top = -(12 - top);
            }

            if (bottom > 6) {
                bottom = -(12 - bottom);
            }

            sequence.add(String.format("(%d,%d)", top, bottom));
        }

        String[] sequenceArray = new String[sequence.size()];
        sequence.toArray(sequenceArray);

        return sequenceArray;
    }

    public String[] generate(State state) {
        ArrayList<String> sequence = new ArrayList<String>();

        int top = 0;
        int bottom = 0;
        int[] solution = solution(state);
        for (int i = solution.length - 1; i >= 0; i--) {
            if (solution[i] < 11) {
                top += 12 - (solution[i] + 1);
                top %= 12;
            } else if (solution[i] < 22) {
                bottom += 12 - ((solution[i] - 11) + 1);
                bottom %= 12;
            } else {
                if (top != 0 || bottom != 0) {
                    if (top > 6) {
                        top = -(12 - top);
                    }

                    if (bottom > 6) {
                        bottom = -(12 - bottom);
                    }

                    sequence.add(String.format("(%d,%d)", top, bottom));
                    top = 0;
                    bottom = 0;
                }

                sequence.add("/");
            }
        }

        if (top != 0 || bottom != 0) {
            if (top > 6) {
                top = -(12 - top);
            }

            if (bottom > 6) {
                bottom = -(12 - bottom);
            }

            sequence.add(String.format("(%d,%d)", top, bottom));
        }

        String[] sequenceArray = new String[sequence.size()];
        sequence.toArray(sequenceArray);

        return sequenceArray;
    }

    private int[] solution(State state) {
        if (!this.initialized) {
            initialize();
        }

        for (int depth = 0;; depth++) {
            ArrayList<Integer> solution1 = new ArrayList<Integer>();
            ArrayList<Integer> solution2 = new ArrayList<Integer>();
            if (search(state, isEvenPermutation(state.getPiecesPermutation()), depth, solution1, solution2)) {
                ArrayList<Integer> sequence = new ArrayList<Integer>();

                for (int moveIndex : solution1) {
                    sequence.add(moveIndex);
                }

                int[][] phase2MoveMapping = {
                    {  2 },
                    {  5 },
                    {  8 },
                    { 13 },
                    { 16 },
                    { 19 },
                    {  0, 22, 10 },
                    { 21, 22, 11 },
                };

                for (int moveIndex : solution2) {
                    for (int phase1MoveIndex : phase2MoveMapping[moveIndex]) {
                        sequence.add(phase1MoveIndex);
                    }
                }

                int[] sequenceArray = new int[sequence.size()];
                for (int i = 0; i < sequenceArray.length; i++) {
                    sequenceArray[i] = sequence.get(i);
                }

                return sequenceArray;
            }
        }
    }

    private boolean search(State state, boolean isEvenPermutation, int depth, ArrayList<Integer> solution1, ArrayList<Integer> solution2) {
        if (depth == 0) {
            if (isEvenPermutation && state.getShapeIndex() == State.id.getShapeIndex()) {
                int[] sequence2 = solution2(state.toCubeState(), 17);
                if (sequence2 != null) {
                    for (int m : sequence2) {
                        solution2.add(m);
                    }

                    return true;
                }
            }

            return false;
        }

        int distance =
            isEvenPermutation ?
                this.evenShapeDistance.get(state.getShapeIndex()) :
                this.oddShapeDistance.get(state.getShapeIndex());
        if (distance <= depth) {
            for (int i = 0; i < this.moves1.length; i++) {
                if (i == 22 && !state.isTwistable()) {
                    continue;
                }

                State next = state.multiply(this.moves1[i]);

                solution1.add(i);
                if (search(
                    next,
                    isEvenPermutation(next.getPiecesPermutation()),
                    depth - 1,
                    solution1,
                    solution2)) {
                    return true;
                }
                solution1.remove(solution1.size() - 1);
            }
        }

        return false;
    }

    private int[] solution2(CubeState state, int maxDepth) {
        int cornersPermutation = IndexMapping.permutationToIndex(state.cornersPermutation);

        boolean[] isTopCorner= new boolean[8];
        for (int k = 0; k < isTopCorner.length; k++) {
            isTopCorner[k] = state.cornersPermutation[k] < 4;
        }
        int cornersCombination = IndexMapping.combinationToIndex(isTopCorner, 4);

        int edgesPermutation = IndexMapping.permutationToIndex(state.edgesPermutation);

        boolean[] isTopEdge = new boolean[8];
        for (int k = 0; k < isTopEdge.length; k++) {
            isTopEdge[k] = state.edgesPermutation[k] < 4;
        }

        int edgesCombination = IndexMapping.combinationToIndex(isTopEdge, 4);

        for (int depth = 0; depth <= maxDepth; depth++) {
            int[] solution = new int[depth];
            if (search2(cornersPermutation, cornersCombination, edgesPermutation, edgesCombination, depth, solution)) {
                return solution;
            }
        }

        return null;
    }

    private boolean search2(int cornersPermutation, int cornersCombination, int edgesPermutation, int edgesCombination, int depth, int[] solution) {
        if (depth == 0) {
            return cornersPermutation == 0 && edgesPermutation == 0;
        }

        if (this.cornersDistance[cornersPermutation][edgesCombination] <= depth &&
            this.edgesDistance[edgesPermutation][cornersCombination] <= depth) {
            for (int i = 0; i < this.moves2.length; i++) {
                if (solution.length - depth - 1 >= 0 && solution[solution.length - depth - 1] / 3 == i / 3) {
                    continue;
                }

                solution[solution.length - depth] = i;
                if (search2(
                    this.cornersPermutationMove[cornersPermutation][i],
                    this.cornersCombinationMove[cornersCombination][i],
                    this.edgesPermutationMove[edgesPermutation][i],
                    this.edgesCombinationMove[edgesCombination][i],
                    depth - 1,
                    solution)) {
                    return true;
                }
            }
        }

        return false;
    }

    public State getRandomState(State shape, Random random) {
        byte[] cornersPermutation =
            IndexMapping.indexToPermutation(
                random.nextInt(this.N_CORNERS_PERMUTATIONS), 8);

        byte[] edgesPermutation =
            IndexMapping.indexToPermutation(
                random.nextInt(this.N_EDGES_PERMUTATIONS), 8);

        byte[] permutation = new byte[shape.permutation.length];
        for (int i = 0; i < permutation.length; i++) {
            if (shape.permutation[i] < 8) {
                permutation[i] = cornersPermutation[shape.permutation[i]];
            } else {
                permutation[i] = (byte) (8 + edgesPermutation[shape.permutation[i] - 8]);
            }
        }

        return new State(permutation);
    }

    public State getRandomState(Random random) {
        if (!this.initialized) {
            initialize();
        }

        return getRandomState(
            this.shapes.get(random.nextInt(this.shapes.size())),
            random);
    }
}

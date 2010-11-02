package com.puzzletimer.solvers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Square1ShapeSolver {
    public static class State {
        public int index;

        public State(int index) {
            this.index = index;
        }

        public State(boolean[] cuts) {
            this.index = 0;

            // bottom
            for (int i = 0; i < 12; i++) {
                this.index <<= 1;
                if (cuts[23 - i]) {
                    this.index |= 1;
                }
            }

            // top
            for (int i = 0; i < 12; i++) {
                this.index <<= 1;
                if (cuts[11 - i]) {
                    this.index |= 1;
                }
            }
        }

        private static int rotate(int layer) {
            return ((layer << 1) & 0xFFE) | ((layer >> 11) & 1);
        }

        private int getTop() {
            return this.index & 0xFFF;
        }

        private int getBottom() {
            return (this.index >> 12) & 0xFFF;
        }

        public State rotateTop() {
            return new State((getBottom() << 12) | rotate(getTop()));
        }

        public State rotateBottom() {
            return new State((rotate(getBottom()) << 12) | getTop());
        }

        public State twist() {
            int top = getTop();
            int bottom = getBottom();

            int newTop = (top & 0xF80) | (bottom & 0x7F);
            int newBottom = (bottom & 0xF80) | (top & 0x7F);

            return new State((newBottom << 12) | newTop);
        }

        public boolean isTwistable() {
            int top = getTop();
            int bottom = getBottom();

            return (top & (1 << 0)) != 0 &&
                   (top & (1 << 6)) != 0 &&
                   (bottom & (1 << 0)) != 0 &&
                   (bottom & (1 << 6)) != 0;
        }

        public State applyMove(String move) {
            State state = this;

            if (move.equals("/")) {
                state = state.twist();
            } else {
                Pattern p = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");
                Matcher matcher = p.matcher(move.toString());
                matcher.find();

                int top = Integer.parseInt(matcher.group(1));
                for (int i = 0; i < top + 12; i++) {
                    state = state.rotateTop();
                }

                int bottom = Integer.parseInt(matcher.group(2));
                for (int i = 0; i < bottom + 12; i++) {
                    state = state.rotateBottom();
                }
            }

            return state;
        }

        public State applySequence(String[] sequence) {
            State state = this;
            for (String move : sequence) {
                state = state.applyMove(move);
            }

            return state;
        }

        public static State id;

        static {
            id = new State(new boolean[] {
                true, false, true, true, false, true, true, false, true, true, false, true,
                true, true, false, true, true, false, true, true, false, true, true, false,
            });
        }
    }

    // constants
    public static final int N_POSITIONS = 16777216;

    // distance table
    public static int[] distance;

    static {
        distance = new int[N_POSITIONS];
        for (int i = 0; i < distance.length; i++) {
            distance[i] = -1;
        }

        distance[State.id.index] = 0;

        int nVisitedPositions;
        int depth = 0;
        do {
            nVisitedPositions = 0;

            for (int i = 0; i < distance.length; i++) {
                if (distance[i] == depth) {
                    State state = new State(i);

                    // twist
                    if (state.isTwistable()) {
                        State next = state.twist();
                        if (distance[next.index] == -1) {
                            distance[next.index] = depth + 1;
                            nVisitedPositions++;
                        }
                    }

                    // rotate top
                    State nextTop = new State(i);
                    for (int j = 0; j < 11; j++) {
                        nextTop = nextTop.rotateTop();
                        if (distance[nextTop.index] == -1) {
                            distance[nextTop.index] = depth + 1;
                            nVisitedPositions++;
                        }
                    }

                    // rotate bottom
                    State nextBottom = new State(i);
                    for (int j = 0; j < 11; j++) {
                        nextBottom = nextBottom.rotateBottom();
                        if (distance[nextBottom.index] == -1) {
                            distance[nextBottom.index] = depth + 1;
                            nVisitedPositions++;
                        }
                    }
                }
            }

            depth++;
        } while (nVisitedPositions > 0);
    }

    public static String[] solve(State state) {
        ArrayList<String> sequence = new ArrayList<String>();

        while (distance[state.index] > 0) {
            // twist
            if (state.isTwistable()) {
                State next = state.twist();
                if (distance[next.index] == distance[state.index] - 1) {
                    sequence.add("/");
                    state = next;
                }
            }

            // rotate top
            int x = 0;
            State nextTop = new State(state.index);
            for (int i = 0; i < 12; i++) {
                if (distance[nextTop.index] == distance[state.index] - 1) {
                    x = i;
                    state = nextTop;
                    break;
                }

                nextTop = nextTop.rotateTop();
            }

            // rotate bottom
            int y = 0;
            State nextBottom = new State(state.index);
            for (int j = 0; j < 12; j++) {
                if (distance[nextBottom.index] == distance[state.index] - 1) {
                    y = j;
                    state = nextBottom;
                    break;
                }

                nextBottom = nextBottom.rotateBottom();
            }

            if (x != 0 || y != 0) {
                sequence.add("(" + (x <= 6 ? x : x - 12) + "," + (y <= 6 ? y : y - 12) + ")");
            }
        }

        String[] sequenceArray = new String[sequence.size()];
        sequence.toArray(sequenceArray);

        return sequenceArray;
    }
}

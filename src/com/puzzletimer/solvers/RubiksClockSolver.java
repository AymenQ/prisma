package com.puzzletimer.solvers;

import java.util.Random;

public class RubiksClockSolver {
    public static class State {
        public int[] clocks;
        public boolean[] pinsDown;

        public State(int[] clocks, boolean[] pinsDown) {
            this.clocks = clocks;
            this.pinsDown = pinsDown;
        }

        public State rotateWheel(boolean[] pinsDown, int wheel, int turns) {
            int[] newClocks = new int[18];
            for (int i = 0; i < newClocks.length; i++) {
                newClocks[i] = this.clocks[i];
            }

            // front
            boolean[] affectedClocks = new boolean[18];
            for (int i = 0; i < affectedClocks.length; i++) {
                affectedClocks[i] = false;
            }

            if (pinsDown[wheel]) {
                for (int i = 0; i < 4; i++) {
                    if (pinsDown[i]) {
                        affectedClocks[wheelsClockFront[i]] = true;
                    }
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (!pinsDown[i]) {
                        for (int clock : pinsClocksFront[i]) {
                            affectedClocks[clock] = true;
                        }
                    }
                }
            }

            for (int i = 0; i < this.clocks.length; i++) {
                if (affectedClocks[i]) {
                    newClocks[i] = (newClocks[i] + 12 + turns) % 12;
                }
            }

            // back
            for (int i = 0; i < affectedClocks.length; i++) {
                affectedClocks[i] = false;
            }

            if (!pinsDown[wheel]) {
                for (int i = 0; i < 4; i++) {
                    if (!pinsDown[i]) {
                        affectedClocks[wheelsClockBack[i]] = true;
                    }
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (pinsDown[i]) {
                        for (int clock : pinsClocksBack[i]) {
                            affectedClocks[clock] = true;
                        }
                    }
                }
            }

            for (int i = 0; i < this.clocks.length; i++) {
                if (affectedClocks[i]) {
                    newClocks[i] = (newClocks[i] + 12 - turns) % 12;
                }
            }

            return new State(newClocks, pinsDown);
        }

        public static int[] wheelsClockFront;
        public static int[] wheelsClockBack;
        public static int[][] pinsClocksFront;
        public static int[][] pinsClocksBack;
        public static State id;

        static {
            wheelsClockFront = new int[] {
                0, 2, 6, 8
            };

            wheelsClockBack = new int[] {
                11, 9, 17, 15
            };

            pinsClocksFront = new int[][] {
                { 0, 1, 3, 4 },
                { 1, 2, 4, 5 },
                { 3, 4, 6, 7 },
                { 4, 5, 7, 8 },
            };

            pinsClocksBack = new int[][] {
                { 10, 11, 13, 14 },
                {  9, 10, 12, 13 },
                { 13, 14, 16, 17 },
                { 12, 13, 15, 16 },
            };

            id = new State(
                new int[] {
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0,

                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0,
                },
                new boolean[] {
                    false, false,
                    false, false,
                });
        }
    }

    public static String[] generate(State state) {
        int[][] inverseMatrix = {
           {  0,  0,  0,  0,  1,  0,  0, -1,  0,  0,  0,  0,  0,  0 },
           {  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0 },
           {  0,  0,  0, -1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
           {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, -1,  0,  0 },
           {  0, -1,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
           {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  0,  1 },
           {  0,  0,  0,  0,  1, -1,  0,  0,  0,  0,  0,  0,  0,  0 },
           {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  1,  0 },
           { -1,  1,  0,  1, -1,  0,  0,  0,  0, -1,  0,  1, -1,  0 },
           {  0,  1, -1,  0, -1,  1,  0,  0,  0, -1, -1,  1,  0,  0 },
           {  0,  0,  0,  0, -1,  1,  0,  1, -1,  0, -1,  1,  0, -1 },
           {  0,  0,  0,  1, -1,  0, -1,  1,  0,  0,  0,  1, -1, -1 },
           {  1, -1,  1, -1,  1, -1,  1, -1,  1,  2,  2, -4,  2,  2 },
           {  0,  0,  0,  0,  0,  0,  0,  0,  0, -1, -1,  3, -1, -1 },
        };

        int[] independentClocks = {
             0,  1,  2,
             3,  4,  5,
             6,  7,  8,

                10,
            12, 13, 14,
                17,
        };

        int[] turns = new int[14];
        for (int i = 0; i < 14; i++) {
            turns[i] = 0;
            for (int j = 0; j < 14; j++) {
                turns[i] += inverseMatrix[i][j] * state.clocks[independentClocks[j]];
            }

            while (turns[i] < -5 || turns[i] > 6) {
                if (turns[i] < 0) {
                    turns[i] += 12;
                } else {
                    turns[i] -= 12;
                }
            }
        }

        String[] sequence = new String[10];
        sequence[0] = "UUdd " + "u=" + turns[0] + ",d=" + turns[1];
        sequence[1] = "dUdU " + "u=" + turns[2] + ",d=" + turns[3];
        sequence[2] = "ddUU " + "u=" + turns[4] + ",d=" + turns[5];
        sequence[3] = "UdUd " + "u=" + turns[6] + ",d=" + turns[7];
        sequence[4] = "dUUU " + "u=" + turns[8];
        sequence[5] = "UdUU " + "u=" + turns[9];
        sequence[6] = "UUUd " + "u=" + turns[10];
        sequence[7] = "UUdU " + "u=" + turns[11];
        sequence[8] = "UUUU " + "u=" + turns[12];
        sequence[9] = "dddd " + "d=" + turns[13];

        return sequence;
    }

    public static String[] getRandomSequence(Random r) {
        int[] clocks = new int[18];
        for (int i = 0; i < clocks.length; i++) {
            clocks[i] = r.nextInt(12);
        }

        String[] generator = generate(new State(clocks, null));

        String pins = "";
        for (int i = 0; i < 4; i++) {
            pins += r.nextBoolean() ? "U" : "d";
        }

        String[] sequence = new String[generator.length + 1];
        for (int i = 0; i < generator.length; i++) {
            sequence[i] = generator[i];
        }
        sequence[sequence.length - 1] = pins;

        return sequence;
    }
}

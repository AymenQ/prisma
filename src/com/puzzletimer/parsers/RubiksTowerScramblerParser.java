package com.puzzletimer.parsers;

import java.util.ArrayList;
import java.util.HashMap;

public class RubiksTowerScramblerParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "RUBIKS-TOWER";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String face = parser.anyChar("BDFLRU");
            if (face != null) {
                move += face;
            } else {
                break;
            }

            String wide = parser.anyChar("w");
            if (wide != null) {
                move += wide;
            }

            String suffix = parser.anyChar("2\'");
            if (suffix != null) {
                move += suffix;
            }

            moves.add(move);
        }

        if (!isValidScramble(moves)) {
            return new String[0];
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }

    private class State {
        public byte[] edgesPermutation;
        public byte[] edgesOrientation;

        public State(byte[] permutation, byte[] orientation) {
            this.edgesOrientation = orientation;
            this.edgesPermutation = permutation;
        }

        public State multiply(State move) {
            byte[] edgesPermutation = new byte[8];
            byte[] edgesOrientation = new byte[8];

            for (int i = 0; i < 8; i++) {
                edgesPermutation[i] = this.edgesPermutation[move.edgesPermutation[i]];
                edgesOrientation[i] = (byte) ((this.edgesOrientation[move.edgesPermutation[i]] + move.edgesOrientation[i]) % 3);
            }

            return new State(edgesPermutation, edgesOrientation);
        }
    }

    private boolean isValidScramble(ArrayList<String> moves) {
        State moveUw = new State(new byte[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveDw = new State(new byte[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveLw = new State(new byte[] { 4, 1, 2, 0, 7, 5, 6, 3 }, new byte[] { 2, 0, 0, 1, 1, 0, 0, 2 });
        State moveRw = new State(new byte[] { 0, 2, 6, 3, 4, 1, 5, 7 }, new byte[] { 0, 1, 2, 0, 0, 2, 1, 0 });
        State moveFw = new State(new byte[] { 0, 1, 3, 7, 4, 5, 2, 6 }, new byte[] { 0, 0, 1, 2, 0, 0, 2, 1 });
        State moveBw = new State(new byte[] { 1, 5, 2, 3, 0, 4, 6, 7 }, new byte[] { 1, 2, 0, 0, 2, 1, 0, 0 });

        HashMap<String, State> moveTable = new HashMap<String, State>();
        moveTable.put("Uw",  moveUw);
        moveTable.put("Uw2", moveUw.multiply(moveUw));
        moveTable.put("Uw'", moveUw.multiply(moveUw).multiply(moveUw));
        moveTable.put("Dw",  moveDw);
        moveTable.put("Dw2", moveDw.multiply(moveDw));
        moveTable.put("Dw'", moveDw.multiply(moveDw).multiply(moveDw));
        moveTable.put("Lw",  moveLw);
        moveTable.put("Lw2", moveLw.multiply(moveLw));
        moveTable.put("Lw'", moveLw.multiply(moveLw).multiply(moveLw));
        moveTable.put("Rw",  moveRw);
        moveTable.put("Rw2", moveRw.multiply(moveRw));
        moveTable.put("Rw'", moveRw.multiply(moveRw).multiply(moveRw));
        moveTable.put("Fw",  moveFw);
        moveTable.put("Fw2", moveFw.multiply(moveFw));
        moveTable.put("Fw'", moveFw.multiply(moveFw).multiply(moveFw));
        moveTable.put("Bw",  moveBw);
        moveTable.put("Bw2", moveBw.multiply(moveBw));
        moveTable.put("Bw'", moveBw.multiply(moveBw).multiply(moveBw));

        State state = new State(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });

        for (String move : moves) {
            byte[] validOrientation = null;

            if (move.equals("U") || move.equals("U2") || move.equals("U'")) {
                validOrientation = new byte[] {  0,  0,  0,  0, -1, -1, -1, -1 };
            }

            if (move.equals("D") || move.equals("D2") || move.equals("D'")) {
                validOrientation = new byte[] { -1, -1, -1, -1,  0,  0,  0,  0 };
            }

            if (move.equals("L") || move.equals("L2") || move.equals("R'")) {
                validOrientation = new byte[] {  1, -1, -1,  2,  2, -1, -1,  1 };
            }

            if (move.equals("R") || move.equals("R2") || move.equals("R'")) {
                validOrientation = new byte[] { -1,  2,  1, -1, -1,  1,  2, -1 };
            }

            if (move.equals("F") || move.equals("F2") || move.equals("F'")) {
                validOrientation = new byte[] { -1, -1,  2,  1, -1, -1,  1,  2 };
            }

            if (move.equals("B") || move.equals("B2") || move.equals("B'")) {
                validOrientation = new byte[] {  2,  1, -1, -1,  1,  2, -1, -1 };
            }

            if (validOrientation != null) {
                for (int i = 0; i < state.edgesOrientation.length; i++) {
                    if (validOrientation[i] != -1 && state.edgesOrientation[i] != validOrientation[i]) {
                        return false;
                    }
                }
            } else {
                state = state.multiply(moveTable.get(move));
            }
        }

        return true;
    }
}

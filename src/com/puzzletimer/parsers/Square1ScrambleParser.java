package com.puzzletimer.parsers;

import java.util.ArrayList;

import com.puzzletimer.solvers.Square1ShapeSolver;

public class Square1ScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "SQUARE-1";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            // twist
            String twist = parser.string("/");
            if (twist != null) {
                moves.add(twist);
                continue;
            }

            // top/bottom turn
            String open = parser.string("(");
            if (open != null) {
                String move = open;

                parser.skipSpaces();

                String top = parser.number();
                if (top != null) {
                    move += top;
                } else {
                    break;
                }

                parser.skipSpaces();

                String comma = parser.string(",");
                if (comma != null) {
                    move += comma;
                } else {
                    break;
                }

                parser.skipSpaces();

                String bottom = parser.number();
                if (bottom != null) {
                    move += bottom;
                } else {
                    break;
                }

                parser.skipSpaces();

                String close = parser.string(")");
                if (close != null) {
                    move += close;
                } else {
                    break;
                }

                moves.add(move);
                continue;
            }

            break;
        }

        moves = fixImplicitTwists(moves);

        if (!isValidScramble(moves)) {
            return new String[0];
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);

        return movesArray;
    }

    private ArrayList<String> fixImplicitTwists(ArrayList<String> sequence) {
        boolean implicit = true;
        for (String move : sequence) {
            if (move.equals("/")) {
                implicit = false;
            }
        }

        if (!implicit) {
            return sequence;
        }

        ArrayList<String> newSequence = new ArrayList<String>();
        for (String move : sequence) {
            newSequence.add(move);
            newSequence.add("/");
        }

        return newSequence;
    }

    private boolean isValidScramble(ArrayList<String> sequence) {
        Square1ShapeSolver.State state = Square1ShapeSolver.State.id;
        for (String move : sequence) {
            if (move.equals("/")) {
                if (!state.isTwistable()) {
                    return false;
                }
            }

            state = state.applyMove(move);
        }

        return true;
    }
}

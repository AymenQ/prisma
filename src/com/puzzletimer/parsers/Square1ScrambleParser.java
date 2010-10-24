package com.puzzletimer.parsers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);

        if (!isValidScramble(movesArray)) {
            movesArray = new String[0];
        }

        return movesArray;
    }

    private void turnTop(boolean[] state, int n) {
        if (n < 0) {
            n += 12;
        }

        for (int i = 0; i < n; i++) {
            boolean temp = state[0];
            for (int j = 0; j < 11; j++) {
                state[j] = state[j + 1];
            }
            state[11] = temp;
        }
    }

    private void turnBottom(boolean[] state, int n) {
        if (n < 0) {
            n += 12;
        }

        for (int i = 0; i < n; i++) {
            boolean temp = state[12];
            for (int j = 0; j < 11; j++) {
                state[j + 12] = state[j + 13];
            }
            state[23] = temp;
        }
    }

    private void twist(boolean[] state) {
        for (int i = 0; i < 5; i++) {
            boolean temp = state[i + 1];
            state[i + 1] = state[i + 13];
            state[i + 13] = temp;
        }
    }

    private boolean isTwistable(boolean[] state) {
        return state[0] && state[6] && state[12] && state[18];
    }

    private boolean isValidScramble(String[] sequence) {
        boolean[] state = {
            // top
            true, true, false, true, true, false, true, true, false, true, true, false,

            // bottom
            true, false, true, true, false, true, true, false, true, true, false, true,
        };

        Pattern p = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");
        for (String move : sequence) {
            if (move.equals("/")) {
                if (!isTwistable(state)) {
                    return false;
                }

                twist(state);
            } else {
                Matcher matcher = p.matcher(move);
                matcher.find();

                turnTop(state, Integer.parseInt(matcher.group(1)));
                turnBottom(state, Integer.parseInt(matcher.group(2)));
            }
        }

        return true;
    }
}

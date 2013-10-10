package com.puzzletimer.parsers;

import java.util.ArrayList;

public class SS9ScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "9x9x9-CUBE";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String slice = parser.anyChar("234");
            if (slice != null) {
                move += slice;
            }

            String face = parser.anyChar("BDFLRU");
            if (face != null) {
                move += face;
            } else {
                break;
            }

            String suffix = parser.anyChar("2\'");
            if (suffix != null) {
                move += suffix;

                if (suffix.equals("2")) {
                    // ignore prime
                    parser.string("\'");
                }
            }

            moves.add(move);
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

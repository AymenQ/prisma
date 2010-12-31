package com.puzzletimer.parsers;

import java.util.ArrayList;

public class MegaminxScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "MEGAMINX";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String face = parser.anyChar("UDR");
            if (face != null) {
                move += face;
            } else {
                break;
            }

            if (face.equals("U")) {
                String prime = parser.anyChar("\'");
                if (prime != null) {
                    move += prime;
                }
            } else {
                String orientation = parser.string("++");
                if (orientation == null) {
                    orientation = parser.string("--");
                }

                if (orientation != null) {
                    move += orientation;
                } else {
                    break;
                }
            }

            moves.add(move);
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

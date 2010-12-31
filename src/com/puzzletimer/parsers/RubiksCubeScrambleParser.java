package com.puzzletimer.parsers;

import java.util.ArrayList;

public class RubiksCubeScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
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

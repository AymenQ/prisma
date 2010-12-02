package com.puzzletimer.parsers;

import java.util.ArrayList;

public class SkewbScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "SKEWB";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String face = parser.anyChar("LRDB");
            if (face != null) {
                move += face;
            } else {
                break;
            }

            String suffix = parser.anyChar("\'");
            if (suffix != null) {
                move += suffix;
            }

            moves.add(move);
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

package com.puzzletimer.parsers;

import java.util.ArrayList;

public class PyraminxScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "PYRAMINX";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String face = parser.anyChar("bBlLrRuU");
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

package com.puzzletimer.parsers;

import java.util.ArrayList;

public class FloppyCubeScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "FLOPPY-CUBE";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String face = parser.anyChar("DLRU");
            if (face != null) {
                move += face;
            } else {
                break;
            }

            moves.add(move);
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

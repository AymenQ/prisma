package com.puzzletimer.parsers;

import java.util.ArrayList;

public class ProfessorsCubeScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "5x5x5-CUBE";
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

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

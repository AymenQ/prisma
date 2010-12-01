package com.puzzletimer.parsers;

import java.util.ArrayList;

public class TowerCubeScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "TOWER-CUBE";
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

            if (face.equals("U") || face.equals("D")) {
                String suffix = parser.anyChar("2\'");
                if (suffix != null) {
                    move += suffix;
                }
            }

            moves.add(move);
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

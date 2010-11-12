package com.puzzletimer.parsers;

import java.util.ArrayList;

public class RubiksClockScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "RUBIKS-CLOCK";
    }

    @Override
    public String[] parse(String input) {
        Parser parser = new Parser(input);

        ArrayList<String> moves = new ArrayList<String>();

        for (;;) {
            parser.skipSpaces();

            String move = "";

            String pin1 = parser.anyChar("Ud");
            if (pin1 != null) {
                move += pin1;
            } else {
                break;
            }

            String pin2 = parser.anyChar("Ud");
            if (pin2 != null) {
                move += pin2;
            } else {
                break;
            }

            String pin3 = parser.anyChar("Ud");
            if (pin3 != null) {
                move += pin3;
            } else {
                break;
            }

            String pin4 = parser.anyChar("Ud");
            if (pin4 != null) {
                move += pin4;
            } else {
                break;
            }

            parser.skipSpaces();

            String wheel1 = parser.anyChar("ud");
            boolean isValidWheel1 =
                wheel1 != null && (
                wheel1.equals(pin1.toLowerCase()) ||
                wheel1.equals(pin2.toLowerCase()) ||
                wheel1.equals(pin3.toLowerCase()) ||
                wheel1.equals(pin4.toLowerCase()));
            if (isValidWheel1) {
                move += " " + wheel1;

                parser.skipSpaces();

                String equals1 = parser.string("=");
                if (equals1 != null) {
                    move += equals1;
                } else {
                    break;
                }

                parser.skipSpaces();

                String negative1 = parser.string("-");
                if (negative1 != null) {
                    move += negative1;
                }

                String turns1 = parser.number();
                if (turns1 != null) {
                    move += turns1;
                } else {
                    break;
                }

                parser.skipSpaces();

                String comma = parser.string(",");
                if (comma != null) {
                    move += comma;

                    parser.skipSpaces();

                    String wheel2 = parser.anyChar("ud");
                    boolean isValidWheel2 =
                        wheel2 != null &&
                        !wheel2.equals(wheel1) && (
                        wheel2.equals(pin1.toLowerCase()) ||
                        wheel2.equals(pin2.toLowerCase()) ||
                        wheel2.equals(pin3.toLowerCase()) ||
                        wheel2.equals(pin4.toLowerCase()));
                    if (isValidWheel2) {
                        move += wheel2;
                    } else {
                        break;
                    }

                    parser.skipSpaces();

                    String equals2 = parser.string("=");
                    if (equals2 != null) {
                        move += equals2;
                    } else {
                        break;
                    }

                    parser.skipSpaces();

                    String negative2 = parser.string("-");
                    if (negative2 != null) {
                        move += negative2;
                    }

                    String turns2 = parser.number();
                    if (turns2 != null) {
                        move += turns2;
                    } else {
                        break;
                    }
                }
            }

            parser.skipSpaces();

            parser.string("/");

            moves.add(move);
        }

        String[] movesArray = new String[moves.size()];
        moves.toArray(movesArray);
        return movesArray;
    }
}

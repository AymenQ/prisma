package com.puzzletimer.tips;

import static com.puzzletimer.Internationalization._;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeSolver.State;

public class RubiksCubeClassicPochmannEdges implements Tip {
    @Override
    public String getTipId() {
        return "RUBIKS-CUBE-CLASSIC-POCHMANN-EDGES";
    }

    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTipDescription() {
        return _("tip.RUBIKS-CUBE-CLASSIC-POCHMANN-EDGES");
    }

    @Override
    public String getTip(Scramble scramble) {
        State state = State.id.applySequence(scramble.getSequence());

        // pieces already solved
        boolean[] solved = new boolean[12];
        for (int i = 0; i < solved.length; i++) {
            solved[i] = i == 5 || (state.edgesPermutation[i] == i && state.edgesOrientation[i] == 0);
        }

        // sticker sequence
        ArrayList<String> stickerSequence = new ArrayList<String>();

        String[][] stickerNames = {
            { "BL", "LB" },
            { "BR", "RB" },
            { "FR", "RF" },
            { "FL", "LF" },
            { "UB", "BU" },
            { "UR", "RU" },
            { "UF", "FU" },
            { "UL", "LU" },
            { "DB", "BD" },
            { "DR", "RD" },
            { "DF", "FD" },
            { "DL", "LD" },
        };

        int cycleFirstPiece = 5;
        int currentPermutation = 5;
        int currentOrientation = 0;
        for (;;) {
            int nextPermutation = state.edgesPermutation[currentPermutation];
            int nextOrientation = (2 - state.edgesOrientation[currentPermutation] + currentOrientation) % 2;

            // break into a new cycle
            if (nextPermutation == cycleFirstPiece) {
                if (cycleFirstPiece != 5) {
                    stickerSequence.add(stickerNames[nextPermutation][nextOrientation]);
                }

                boolean allPiecesSolved = true;
                for (int i = 0; i < 12; i++) {
                    if (!solved[i]) {
                        cycleFirstPiece = i;
                        currentPermutation = i;
                        currentOrientation = 0;

                        nextPermutation = i;
                        nextOrientation = 0;

                        allPiecesSolved = false;
                        break;
                    }
                }

                if (allPiecesSolved) {
                    break;
                }
            }

            stickerSequence.add(stickerNames[nextPermutation][nextOrientation]);

            currentPermutation = nextPermutation;
            currentOrientation = nextOrientation;
            solved[currentPermutation] = true;
        }

        // solution
        StringBuilder tip = new StringBuilder();

        tip.append(_("tip.RUBIKS-CUBE-CLASSIC-POCHMANN-EDGES") + ":\n");
        tip.append("  [T1]  R U R' U' R' F R2 U' R' U' R U R' F'\n");
        tip.append("  [T2]  x' R2 U' R' U x R' F' U' F R U R' U'\n");
        tip.append("  [J1]  R U R' F' R U R' U' R' F R2 U' R' U'\n");
        tip.append("  [J2]  R' U2 R U R' U2 L U' R U L'\n");
        tip.append("\n");

        HashMap<String, String> letteringScheme = new HashMap<String, String>();
        letteringScheme.put("UB", "A");
        letteringScheme.put("UR", "B");
        letteringScheme.put("UF", "C");
        letteringScheme.put("UL", "D");
        letteringScheme.put("LU", "E");
        letteringScheme.put("LF", "F");
        letteringScheme.put("LD", "G");
        letteringScheme.put("LB", "H");
        letteringScheme.put("FU", "I");
        letteringScheme.put("FR", "J");
        letteringScheme.put("FD", "K");
        letteringScheme.put("FL", "L");
        letteringScheme.put("RU", "M");
        letteringScheme.put("RB", "N");
        letteringScheme.put("RD", "O");
        letteringScheme.put("RF", "P");
        letteringScheme.put("BU", "Q");
        letteringScheme.put("BL", "R");
        letteringScheme.put("BD", "S");
        letteringScheme.put("BR", "T");
        letteringScheme.put("DF", "U");
        letteringScheme.put("DR", "V");
        letteringScheme.put("DB", "W");
        letteringScheme.put("DL", "X");

        HashMap<String, String> solutions = new HashMap<String, String>();
        solutions.put("UB", "[J2]");
        solutions.put("BU", "l [J1] l'");
        solutions.put("UF", "[J1]");
        solutions.put("FU", "l' [J2] l");
        solutions.put("UL", "[T1]");
        solutions.put("LU", "[T2]");
        solutions.put("BL", "L [T1] L'");
        solutions.put("LB", "L [T2] L'");
        solutions.put("BR", "d2 L' [T1] L d2");
        solutions.put("RB", "d L [T1] L' d'");
        solutions.put("FR", "d2 L [T1] L' d2");
        solutions.put("RF", "d' L' [T1] L d");
        solutions.put("FL", "L' [T1] L");
        solutions.put("LF", "L' [T2] L");
        solutions.put("DB", "l2 [J1] l2");
        solutions.put("BD", "l [J2] l'");
        solutions.put("DR", "S' [T1] S");
        solutions.put("RD", "D' l' [J1] l D");
        solutions.put("DF", "l2 [J2] l2");
        solutions.put("FD", "l' [J1] l");
        solutions.put("DL", "L2 [T1] L2");
        solutions.put("LD", "L2 [T2] L2");

        for (String sticker : stickerSequence) {
            tip.append("  (UR " + sticker + ") " + letteringScheme.get(sticker) + "  " + solutions.get(sticker) + "\n");
        }

        return tip.toString().trim();
    }

    @Override
    public String toString() {
        return getTipDescription();
    }
}

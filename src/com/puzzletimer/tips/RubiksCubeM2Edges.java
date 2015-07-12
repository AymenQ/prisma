package com.puzzletimer.tips;

import static com.puzzletimer.Internationalization._;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeSolver.State;

public class RubiksCubeM2Edges implements Tip {
    @Override
    public String getTipId() {
        return "RUBIKS-CUBE-M2-EDGES";
    }

    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTipDescription() {
        return _("tip.RUBIKS-CUBE-M2-EDGES");
    }

    @Override
    public String getTip(Scramble scramble) {
        State state = State.id.applySequence(scramble.getSequence());

        // pieces already solved
        boolean[] solved = new boolean[12];
        for (int i = 0; i < solved.length; i++) {
            solved[i] = i == 10 || (state.edgesPermutation[i] == i && state.edgesOrientation[i] == 0);
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

        int cycleFirstPiece = 10;
        int currentPermutation = 10;
        int currentOrientation = 0;
        for (;;) {
            int nextPermutation = state.edgesPermutation[currentPermutation];
            int nextOrientation = (2 - state.edgesOrientation[currentPermutation] + currentOrientation) % 2;

            // break into a new cycle
            if (nextPermutation == cycleFirstPiece) {
                if (cycleFirstPiece != 10) {
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

        tip.append(_("tip.RUBIKS-CUBE-M2-EDGES")).append(":\n");

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
        solutions.put("UB", "M2");
        solutions.put("UR", "R' U R U' M2 U R' U' R");
        solutions.put("UF", "U2 M' U2 M'");
        solutions.put("UL", "L U' L' U M2 U' L U L'");
        solutions.put("LU", "x' U L' U' M2 U L U' x");
        solutions.put("LF", "x' U L2' U' M2 U L2 U' x");
        solutions.put("LD", "x' U L U' M2 U L' U' x");
        solutions.put("LB", "r' U L U' M2 U L' U' r");
        solutions.put("FU", "F E R U R' E' R U' R' F' M2");
        solutions.put("FR", "U R U' M2 U R' U'");
        solutions.put("FL", "U' L' U M2 U' L U");
        solutions.put("RU", "x' U' R U M2 U' R' U x");
        solutions.put("RB", "l U' R' U M2 U' R U l'");
        solutions.put("RD", "x' U' R' U M2 U' R U x");
        solutions.put("RF", "x' U' R2 U M2 U' R2 U x");
        solutions.put("BU", "F' D R' F D' M2 D F' R D' F");
        solutions.put("BL", "U' L U M2 U' L' U");
        solutions.put("BD", "M2 D R' U R' U' M' U R U' M R D'");
        solutions.put("BR", "U R' U' M2 U R U'");
        solutions.put("DR", "U R2 U' M2 U R2 U'");
        solutions.put("DB", "M U2 M U2");
        solutions.put("DL", "U' L2 U M2 U' L2 U");

        HashMap<String, String> mLayerInverse = new HashMap<String, String>();
        mLayerInverse.put("FU", "BD");
        mLayerInverse.put("UF", "DB");
        mLayerInverse.put("BD", "FU");
        mLayerInverse.put("DB", "UF");

        for (int i = 0; i < stickerSequence.size(); i++) {
            String sticker = stickerSequence.get(i);
            tip.append("  (DF ").append(sticker).append(") ").append(letteringScheme.get(sticker));

            if (i % 2 == 1 && mLayerInverse.containsKey(sticker)) {
                sticker = mLayerInverse.get(sticker);
            }

            tip.append("  ").append(solutions.get(sticker)).append("\n");
        }

        return tip.toString().trim();
    }

    @Override
    public String toString() {
        return getTipDescription();
    }
}

package com.puzzletimer.tips;

import static com.puzzletimer.Internationalization._;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeSolver.State;

public class RubiksCubeClassicPochmannCorners implements Tip {
    @Override
    public String getTipId() {
        return "RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS";
    }

    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTipDescription() {
        return _("tip.RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS");
    }

    @Override
    public String getTip(Scramble scramble) {
        State state = State.id.applySequence(scramble.getSequence());

        // pieces already solved
        boolean[] solved = new boolean[8];
        for (int i = 0; i < solved.length; i++) {
            solved[i] = i == 0 || (state.cornersPermutation[i] == i && state.cornersOrientation[i] == 0);
        }

        // sticker sequence
        ArrayList<String> stickerSequence = new ArrayList<String>();

        String[][] stickerNames = {
            { "ULB", "LBU", "BUL" },
            { "UBR", "BRU", "RUB" },
            { "URF", "RFU", "FUR" },
            { "UFL", "FLU", "LUF" },
            { "DBL", "BLD", "LDB" },
            { "DRB", "RBD", "BDR" },
            { "DFR", "FRD", "RDF" },
            { "DLF", "LFD", "FDL" },
        };

        int cycleFirstPiece = 0;
        int currentPermutation = 0;
        int currentOrientation = 1;
        for (;;) {
            int nextPermutation = state.cornersPermutation[currentPermutation];
            int nextOrientation = (3 - state.cornersOrientation[currentPermutation] + currentOrientation) % 3;

            // break into a new cycle
            if (nextPermutation == cycleFirstPiece) {
                if (cycleFirstPiece != 0) {
                    stickerSequence.add(stickerNames[nextPermutation][nextOrientation]);
                }

                boolean allPiecesSolved = true;
                for (int i = 0; i < 8; i++) {
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

        tip.append(_("tip.RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS")).append(":\n");
        tip.append("  [Y]  R U' R' U' R U R' F' R U R' U' R' F R\n");
        tip.append("\n");

        HashMap<String, String> letteringScheme = new HashMap<String, String>();
        letteringScheme.put("ULB", "A");
        letteringScheme.put("UBR", "B");
        letteringScheme.put("URF", "C");
        letteringScheme.put("UFL", "D");
        letteringScheme.put("LBU", "E");
        letteringScheme.put("LUF", "F");
        letteringScheme.put("LFD", "G");
        letteringScheme.put("LDB", "H");
        letteringScheme.put("FLU", "I");
        letteringScheme.put("FUR", "J");
        letteringScheme.put("FRD", "K");
        letteringScheme.put("FDL", "L");
        letteringScheme.put("RFU", "M");
        letteringScheme.put("RUB", "N");
        letteringScheme.put("RBD", "O");
        letteringScheme.put("RDF", "P");
        letteringScheme.put("BRU", "Q");
        letteringScheme.put("BUL", "R");
        letteringScheme.put("BLD", "S");
        letteringScheme.put("BDR", "T");
        letteringScheme.put("DLF", "U");
        letteringScheme.put("DFR", "V");
        letteringScheme.put("DRB", "W");
        letteringScheme.put("DBL", "X");

        HashMap<String, String> solutions = new HashMap<String, String>();
        solutions.put("UBR", "R2 [Y] R2");
        solutions.put("BRU", "R D' [Y] D R'");
        solutions.put("RUB", "R' F [Y] F' R");
        solutions.put("URF", "R2 D' [Y] D R2");
        solutions.put("RFU", "F [Y] F'");
        solutions.put("FUR", "R' [Y] R");
        solutions.put("UFL", "F2 [Y] F2");
        solutions.put("FLU", "F R' [Y] R F'");
        solutions.put("LUF", "F' D [Y] D' F");
        solutions.put("DBL", "D2 [Y] D2");
        solutions.put("BLD", "D F' [Y] F D'");
        solutions.put("LDB", "D' R [Y] R' D");
        solutions.put("DRB", "D' [Y] D");
        solutions.put("RBD", "R2 F [Y] F' R2");
        solutions.put("BDR", "R [Y] R'");
        solutions.put("DFR", "[Y]");
        solutions.put("FRD", "F' R' [Y] R F");
        solutions.put("RDF", "R F [Y] F' R'");
        solutions.put("DLF", "D [Y] D'");
        solutions.put("LFD", "F' [Y] F");
        solutions.put("FDL", "F2 R' [Y] R F2");

        for (String sticker : stickerSequence) {
            tip.append("  (LBU ").append(sticker).append(") ").append(letteringScheme.get(sticker)).append("  ").append(solutions.get(sticker)).append("\n");
        }

        return tip.toString().trim();
    }

    @Override
    public String toString() {
        return getTipDescription();
    }
}

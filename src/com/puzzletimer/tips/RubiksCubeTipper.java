package com.puzzletimer.tips;

import java.util.HashMap;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeCrossSolver;

public class RubiksCubeTipper implements Tipper {
    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTips(Scramble scramble) {
        RubiksCubeCrossSolver.State state =
            RubiksCubeCrossSolver.State.id.applySequence(scramble.getSequence());

        StringBuilder tip = new StringBuilder("Optimal cross:\n");
        for (String[] solution : RubiksCubeCrossSolver.solve(state)) {
            StringBuilder crossSolution = new StringBuilder();
            for (String move : applyX2(solution)) {
                crossSolution.append(move + " ");
            }

            tip.append("  " + crossSolution.toString().trim() +  "\n");
        }

        return tip.toString().trim();
    }

    private String[] applyX2(String[] sequence) {
        HashMap<String, String> table = new HashMap<String, String>();
        table.put("U",  "D");
        table.put("U2", "D2");
        table.put("U'", "D'");
        table.put("D",  "U");
        table.put("D2", "U2");
        table.put("D'", "U'");
        table.put("L",  "L");
        table.put("L2", "L2");
        table.put("L'", "L'");
        table.put("R",  "R");
        table.put("R2", "R2");
        table.put("R'", "R'");
        table.put("F",  "B");
        table.put("F2", "B2");
        table.put("F'", "B'");
        table.put("B",  "F");
        table.put("B2", "F2");
        table.put("B'", "F'");

        String[] newSequence = new String[sequence.length + 1];
        newSequence[0] = "x2";
        for (int i = 1; i < newSequence.length; i++) {
            newSequence[i] = table.get(sequence[i - 1]);
        }

        return newSequence;
    }
}

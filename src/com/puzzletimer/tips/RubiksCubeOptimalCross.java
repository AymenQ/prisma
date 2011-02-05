package com.puzzletimer.tips;

import java.util.HashMap;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeCrossSolver;
import com.puzzletimer.util.StringUtils;

public class RubiksCubeOptimalCross implements Tip {
    @Override
    public String getTipId() {
        return "RUBIKS-CUBE-OPTIMAL-CROSS";
    }

    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTipDescription() {
        return "Optimal cross";
    }

    @Override
    public String getTip(Scramble scramble) {
        RubiksCubeCrossSolver.State crossState =
            RubiksCubeCrossSolver.State.id.applySequence(scramble.getSequence());

        StringBuilder tip = new StringBuilder();

        tip.append("Optimal cross:\n");
        for (String[] solution : RubiksCubeCrossSolver.solve(crossState)) {
            tip.append("  " + StringUtils.join(" ", applyX2(solution)) +  "\n");
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

    @Override
    public String toString() {
        return getTipDescription();
    }
}

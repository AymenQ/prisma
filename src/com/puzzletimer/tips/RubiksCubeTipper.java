package com.puzzletimer.tips;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeCrossSolver;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCubeTipper implements Tipper {
    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTips(Scramble scramble) {
        StringBuilder tip = new StringBuilder();

        // 3op cycles
        tip.append("3OP cycles:\n  ");

        RubiksCubeSolver.State cubeState =
            RubiksCubeSolver.State.id.applySequence(scramble.getSequence());

        // corner cycles
        for (ArrayList<Byte> cycle : cycles(cubeState.cornersPermutation)) {
            if (cycle.size() < 2) {
                continue;
            }

            String[] cornerNames = {
                "UBL", "UBR", "UFR", "UFL",
                "DBL", "DBR", "DFR", "DFL",
            };

            tip.append("(" + cornerNames[cycle.get(0)]);
            for (int i = 1; i < cycle.size(); i++) {
                tip.append(" " + cornerNames[cycle.get(i)]);
            }
            tip.append(")");
        }

        tip.append("\n  ");

        // edges
        for (ArrayList<Byte> cycle : cycles(cubeState.edgesPermutation)) {
            if (cycle.size() < 2) {
                continue;
            }

            String[] edgeNames = {
                "BL", "BR", "FR", "FL",
                "UB", "UR", "UF", "UL",
                "DB", "DR", "DF", "DL",
            };

            tip.append("(" + edgeNames[cycle.get(0)]);
            for (int i = 1; i < cycle.size(); i++) {
                tip.append(" " + edgeNames[cycle.get(i)]);
            }
            tip.append(")");
        }

        tip.append("\n\n");

        // optimal cross
        RubiksCubeCrossSolver.State crossState =
            RubiksCubeCrossSolver.State.id.applySequence(scramble.getSequence());

        tip.append("Optimal cross:\n");
        for (String[] solution : RubiksCubeCrossSolver.solve(crossState)) {
            StringBuilder crossSolution = new StringBuilder();
            for (String move : applyX2(solution)) {
                crossSolution.append(move + " ");
            }

            tip.append("  " + crossSolution.toString().trim() +  "\n");
        }

        return tip.toString().trim();
    }

    private ArrayList<ArrayList<Byte>> cycles(byte[] permutation) {
        boolean[] visited = new boolean[permutation.length];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<Byte>> cycles = new ArrayList<ArrayList<Byte>>();
        for (int i = 0; i < permutation.length; i++) {
            if (visited[i] == true) {
                continue;
            }

            ArrayList<Byte> cycle = new ArrayList<Byte>();
            byte current = (byte) i;
            do {
                cycle.add(current);
                visited[current] = true;
                current = permutation[current];
            } while (current != cycle.get(0));

            cycles.add(cycle);
        }

        return cycles;
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

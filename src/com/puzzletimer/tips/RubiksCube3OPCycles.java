package com.puzzletimer.tips;

import static com.puzzletimer.Internationalization._;

import java.util.ArrayList;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeSolver;

public class RubiksCube3OPCycles implements Tip {
    @Override
    public String getTipId() {
        return "RUBIKS-CUBE-3OP-CYCLES";
    }

    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTipDescription() {
        return _("tip.RUBIKS-CUBE-3OP-CYCLES");
    }

    @Override
    public String getTip(Scramble scramble) {
        StringBuilder tip = new StringBuilder();

        tip.append(_("tip.RUBIKS-CUBE-3OP-CYCLES") + ":\n  ");

        RubiksCubeSolver.State cubeState =
            RubiksCubeSolver.State.id.applySequence(scramble.getSequence());

        // corner cycles
        int[] cornersOrder = { 3, 2, 1, 0, 7, 6, 5, 4 };
        for (ArrayList<Byte> cycle : cycles(cornersOrder, cubeState.cornersPermutation)) {
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

        // edges cycles
        int[] edgesOrder = { 6, 7, 4, 5, 3, 0, 1, 2, 10, 11, 8, 9 };
        for (ArrayList<Byte> cycle : cycles(edgesOrder, cubeState.edgesPermutation)) {
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

        return tip.toString().trim();
    }

    private ArrayList<ArrayList<Byte>> cycles(int[] order, byte[] permutation) {
        boolean[] visited = new boolean[permutation.length];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<Byte>> cycles = new ArrayList<ArrayList<Byte>>();
        for (int i = 0; i < permutation.length; i++) {
            if (visited[order[i]] == true) {
                continue;
            }

            ArrayList<Byte> cycle = new ArrayList<Byte>();
            byte current = (byte) order[i];
            do {
                cycle.add(current);
                visited[current] = true;
                current = permutation[current];
            } while (current != cycle.get(0));

            cycles.add(cycle);
        }

        return cycles;
    }

    @Override
    public String toString() {
        return getTipDescription();
    }
}

package com.puzzletimer.tips;

import static com.puzzletimer.Internationalization._;

import java.util.ArrayList;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeXCrossSolver;
import com.puzzletimer.solvers.RubiksCubeSolver.State;
import com.puzzletimer.util.StringUtils;

public class RubiksCubeOptimalXCross implements Tip {
    private static State x;
    private static State y;
    private static State z;

    static {
        x = new State(
            new byte[] { 3, 2, 6, 7, 0, 1, 5, 4 },
            new byte[] { 2, 1, 2, 1, 1, 2, 1, 2 },
            new byte[] { 7, 5, 9, 11, 6, 2, 10, 3, 4, 1, 8, 0 },
            new byte[] { 0, 0, 0,  0, 1, 0,  1, 0, 1, 0, 1, 0 });

        y = new State(
            new byte[] { 3, 0, 1, 2, 7, 4, 5, 6 },
            new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 },
            new byte[] { 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10 },
            new byte[] { 1, 1, 1, 1, 0, 0, 0, 0,  0, 0, 0,  0 });

        z = new State(
            new byte[] { 4, 0, 3, 7, 5, 1, 2, 6 },
            new byte[] { 1, 2, 1, 2, 2, 1, 2, 1 },
            new byte[] { 8, 4, 6, 10, 0, 7, 3, 11, 1, 5, 2, 9 },
            new byte[] { 1, 1, 1,  1, 1, 1, 1, 1,  1, 1, 1, 1 });
    }

    @Override
    public String getTipId() {
        return "RUBIKS-CUBE-OPTIMAL-X-CROSS";
    }

    @Override
    public String getPuzzleId() {
        return "RUBIKS-CUBE";
    }

    @Override
    public String getTipDescription() {
        return _("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS");
    }

    @Override
    public String getTip(Scramble scramble) {
        State state = State.id.applySequence(scramble.getSequence());

        StringBuilder tip = new StringBuilder();

        // x-cross on U
        State stateU =
            x.multiply(x).multiply(state).multiply(x).multiply(x);
        tip.append(_("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS.optimal_x_cross_on_u")).append(":\n");
        tip.append(getOptimalSolutions(stateU, "x2 "));
        tip.append("\n");

        // x-cross on D
        State stateD = state;
        tip.append(_("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS.optimal_x_cross_on_d")).append(":\n");
        tip.append(getOptimalSolutions(stateD, ""));
        tip.append("\n");

        // x-cross on L
        State stateL =
            z.multiply(state).multiply(z).multiply(z).multiply(z);
        tip.append(_("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS.optimal_x_cross_on_l")).append(":\n");
        tip.append(getOptimalSolutions(stateL, "z' "));
        tip.append("\n");

        // x-cross on R
        State stateR =
            z.multiply(z).multiply(z).multiply(state).multiply(z);
        tip.append(_("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS.optimal_x_cross_on_r")).append(":\n");
        tip.append(getOptimalSolutions(stateR, "z "));
        tip.append("\n");

        // x-cross on F
        State stateF =
            x.multiply(state).multiply(x).multiply(x).multiply(x);
        tip.append(_("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS.optimal_x_cross_on_f")).append(":\n");
        tip.append(getOptimalSolutions(stateF, "x' "));
        tip.append("\n");

        // x-cross on B
        State stateB =
            x.multiply(x).multiply(x).multiply(state).multiply(x);
        tip.append(_("tip.RUBIKS-CUBE-OPTIMAL-X-CROSS.optimal_x_cross_on_b")).append(":\n");
        tip.append(getOptimalSolutions(stateB, "x "));
        tip.append("\n");

        return tip.toString().trim();
    }

    private String getOptimalSolutions(State state, String prefix) {
        ArrayList<String> prefixes = new ArrayList<String>();
        ArrayList<String[]> solutions = new ArrayList<String[]>();

        // id
        State stateId = state;
        for (String[] solution : RubiksCubeXCrossSolver.solve(stateId)) {
            prefixes.add(prefix);
            solutions.add(solution);
        }

        // y
        State stateY =
            y.multiply(y).multiply(y).multiply(state).multiply(y);
        for (String[] solution : RubiksCubeXCrossSolver.solve(stateY)) {
            prefixes.add(prefix + "y ");
            solutions.add(solution);
        }

        // y2
        State stateY2 =
            y.multiply(y).multiply(state).multiply(y).multiply(y);
        for (String[] solution : RubiksCubeXCrossSolver.solve(stateY2)) {
            prefixes.add(prefix + "y2 ");
            solutions.add(solution);
        }

        // y'
        State stateY3 =
            y.multiply(state).multiply(y).multiply(y).multiply(y);
        for (String[] solution : RubiksCubeXCrossSolver.solve(stateY3)) {
            prefixes.add(prefix + "y' ");
            solutions.add(solution);
        }

        int minLength = Integer.MAX_VALUE;
        for (String[] solution : solutions) {
            if (solution.length < minLength) {
                minLength = solution.length;
            }
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < solutions.size(); i++) {
            if (solutions.get(i).length == minLength) {
                output.append(
                    String.format("  %s%s\n",
                        prefixes.get(i),
                        StringUtils.join(" ", solutions.get(i))));
            }
        }

        return output.toString();
    }

    @Override
    public String toString() {
        return getTipDescription();
    }
}

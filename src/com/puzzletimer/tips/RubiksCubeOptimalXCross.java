package com.puzzletimer.tips;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.RubiksCubeXCrossSolver;
import com.puzzletimer.solvers.RubiksCubeSolver.State;
import com.puzzletimer.util.StringUtils;

public class RubiksCubeOptimalXCross implements Tip {
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
        return "Optimal x-cross";
    }

    @Override
    public String getTip(Scramble scramble) {
        State state = State.id.applySequence(scramble.getSequence());

        StringBuilder tip = new StringBuilder();

        tip.append("Optimal x-cross:\n");
        for (String[] solution : RubiksCubeXCrossSolver.solve(state)) {
            tip.append("  " + StringUtils.join(" ", solution) +  "\n");
        }

        return tip.toString().trim();
    }

    @Override
    public String toString() {
        return getTipDescription();
    }
}

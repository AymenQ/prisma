package com.puzzletimer.tips;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.Square1ShapeSolver;
import com.puzzletimer.util.StringUtils;

public class Square1Tipper implements Tipper {
    @Override
    public String getPuzzleId() {
        return "SQUARE-1";
    }

    @Override
    public String getTips(Scramble scramble) {
        String[] solution = Square1ShapeSolver.solve(
            Square1ShapeSolver.State.id.applySequence(scramble.getSequence()));

        return "Optimal cube shape:\n  " + StringUtils.join(" ", solution);
    }
}

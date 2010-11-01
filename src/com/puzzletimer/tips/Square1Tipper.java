package com.puzzletimer.tips;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.solvers.Square1ShapeSolver;

public class Square1Tipper implements Tipper {
    @Override
    public String getPuzzleId() {
        return "SQUARE-1";
    }

    @Override
    public String getTips(Scramble scramble) {
        Square1ShapeSolver.State state =
            Square1ShapeSolver.State.id.applySequence(scramble.getSequence());

        StringBuilder tip = new StringBuilder("Optimal cube shape:\n  ");
        for (String move : Square1ShapeSolver.solve(state)) {
            tip.append(move + " ");
        }

        return tip.toString().trim();
    }
}

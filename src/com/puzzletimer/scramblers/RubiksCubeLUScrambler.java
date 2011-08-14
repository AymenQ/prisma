package com.puzzletimer.scramblers;

import java.util.HashMap;
import java.util.Random;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import com.puzzletimer.solvers.RubiksCubeRUSolver;

public class RubiksCubeLUScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private Random random;

    public RubiksCubeLUScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
        this.random = new Random();
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return this.scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        HashMap<String, String> mirror = new HashMap<String, String>();
        mirror.put("U",  "U'");
        mirror.put("U2", "U2");
        mirror.put("U'", "U");
        mirror.put("R",  "L'");
        mirror.put("R2", "L2");
        mirror.put("R'", "L");

        String[] sequence =
            RubiksCubeRUSolver.generate(
                RubiksCubeRUSolver.getRandomState(this.random));
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = mirror.get(sequence[i]);
        }

        return new Scramble(
            getScramblerInfo().getScramblerId(),
            sequence);
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

package com.puzzletimer.scramblers;

import java.util.HashMap;

public class Scramblers {
    private static HashMap<String, Scrambler> scramblers;

    static {
        scramblers = new HashMap<String, Scrambler>();
        scramblers.put("2x2x2-CUBE-RANDOM", new RubiksPocketCubeRandomScrambler(12));
        scramblers.put("RUBIKS-CUBE-RANDOM", new RubiksCubeRandomScrambler(25));
        scramblers.put("4x4x4-CUBE-RANDOM", new RubiksRevengeRandomScrambler(40));
        scramblers.put("5x5x5-CUBE-RANDOM", new ProfessorsCubeRandomScrambler(60));
        scramblers.put("MEGAMINX-RANDOM", new MegaminxRandomScrambler());
        scramblers.put("PYRAMINX-RANDOM", new PyraminxRandomScrambler(20));
        scramblers.put("SQUARE-1-RANDOM", new Square1RandomScrambler(20));
    }

    public static Scrambler getScrambler(String scramblerId) {
        return scramblers.get(scramblerId);
    }
}

package com.puzzletimer.scramblers;

import java.util.HashMap;

import com.puzzletimer.models.ScramblerInfo;

public class ScramblerBuilder {
    private static HashMap<String, Scrambler> scramblers;

    static {
        Scrambler[] scramblerList = {
            new EmptyScrambler(new ScramblerInfo("EMPTY", "OTHER", "Empty scrambler")),
            new RubiksPocketCubeRandomScrambler(new ScramblerInfo("2x2x2-CUBE-RANDOM", "2x2x2-CUBE", "Random scrambler")),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM", "RUBIKS-CUBE", "Random scrambler"), false, false, false, false),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-EDGES", "RUBIKS-CUBE", "Random edges scrambler"), true, true, false, false),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-EDGES-PERMUTATION", "RUBIKS-CUBE", "Random edges permutation scrambler"), true, true, false, true),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-EDGES-ORIENTATION", "RUBIKS-CUBE", "Random edges orientation scrambler"), true, true, true, false),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-CORNERS", "RUBIKS-CUBE", "Random corners scrambler"), false, false, true, true),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-CORNERS-PERMUTATION", "RUBIKS-CUBE", "Random corners permutation scrambler"), false, true, true, true),
            new RubiksCubeRandomScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-CORNERS-ORIENTATION", "RUBIKS-CUBE", "Random corners orientation scrambler"), true, false, true, true),
            new RubiksCubeLastLayerScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-LAST-LAYER", "RUBIKS-CUBE", "Random last layer"), false),
            new RubiksCubeLastLayerScrambler(new ScramblerInfo("RUBIKS-CUBE-RANDOM-LAST-LAYER-PERMUTATION", "RUBIKS-CUBE", "Random last layer permutation"), true),
            new RubiksCubeControlledCrossDistanceScrambler(new ScramblerInfo("RUBIKS-CUBE-EASY-CROSS", "RUBIKS-CUBE", "Easy cross scrambler"), 0, 3),
            new RubiksCubeControlledCrossDistanceScrambler(new ScramblerInfo("RUBIKS-CUBE-HARD-CROSS", "RUBIKS-CUBE", "Hard cross scrambler"), 6, 8),
            new RubiksRevengeRandomScrambler(new ScramblerInfo("4x4x4-CUBE-RANDOM", "4x4x4-CUBE", "Random scrambler"), 40),
            new ProfessorsCubeRandomScrambler(new ScramblerInfo("5x5x5-CUBE-RANDOM", "5x5x5-CUBE", "Random scrambler"), 60),
            new MegaminxRandomScrambler(new ScramblerInfo("MEGAMINX-RANDOM", "MEGAMINX", "Random scrambler")),
            new PyraminxRandomScrambler(new ScramblerInfo("PYRAMINX-RANDOM", "PYRAMINX", "Random scrambler")),
            new Square1RandomScrambler(new ScramblerInfo("SQUARE-1-RANDOM", "SQUARE-1", "Random scrambler"), 20),
        };

        scramblers = new HashMap<String, Scrambler>();
        for (Scrambler scrambler : scramblerList) {
            scramblers.put(scrambler.getScramblerInfo().getScramblerId(), scrambler);
        }
    }

    public static Scrambler getScrambler(String scramblerId) {
        return scramblers.get(scramblerId);
    }
}

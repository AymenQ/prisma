package com.puzzletimer.scramblers;

import java.util.HashMap;

public class Scramblers {
    private static HashMap<String, Scrambler> scramblers;

    static {
        Scrambler[] scramblerList = {
            new EmptyScrambler(),
            new RubiksPocketCubeRandomScrambler(),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM", "Random scrambler", false, false, false, false),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM-EDGES", "Random edges scrambler", true, true, false, false),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM-EDGES-PERMUTATION", "Random edges permutation scrambler", true, true, false, true),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM-EDGES-ORIENTATION", "Random edges orientation scrambler", true, true, true, false),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM-CORNERS", "Random corners scrambler", false, false, true, true),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM-CORNERS-PERMUTATION", "Random corners permutation scrambler", false, true, true, true),
            new RubiksCubeRandomScrambler("RUBIKS-CUBE-RANDOM-CORNERS-ORIENTATION", "Random corners orientation scrambler", true, false, true, true),
            new RubiksCubeLastLayerScrambler("RUBIKS-CUBE-RANDOM-LAST-LAYER", "Random last layer", false),
            new RubiksCubeLastLayerScrambler("RUBIKS-CUBE-RANDOM-LAST-LAYER-PERMUTATION", "Random last layer permutation", true),
            new RubiksCubeControlledCrossDistanceScrambler("RUBIKS-CUBE-EASY-CROSS", "Easy cross scrambler", 0, 3),
            new RubiksCubeControlledCrossDistanceScrambler("RUBIKS-CUBE-HARD-CROSS", "Hard cross scrambler", 6, 8),
            new RubiksRevengeRandomScrambler(40),
            new ProfessorsCubeRandomScrambler(60),
            new MegaminxRandomScrambler(),
            new PyraminxRandomScrambler(),
            new Square1RandomScrambler(20),
        };

        scramblers = new HashMap<String, Scrambler>();
        for (Scrambler scrambler : scramblerList) {
            scramblers.put(scrambler.getScramblerId(), scrambler);
        }
    }

    public static Scrambler getScrambler(String scramblerId) {
        return scramblers.get(scramblerId);
    }
}

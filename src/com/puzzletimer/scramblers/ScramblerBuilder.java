package com.puzzletimer.scramblers;

import java.util.HashMap;

import com.puzzletimer.models.ScramblerInfo;

public class ScramblerBuilder {
    private static Scrambler[] scramblers;
    private static HashMap<String, Scrambler> scramblerMap;

    static {
        // empty
        Scrambler empty =
            new EmptyScrambler(
                new ScramblerInfo("EMPTY", "OTHER", "Empty scrambler"));

        // 2x2x2 random
        Scrambler rubiksPocketCubeRandom =
            new RubiksPocketCubeRandomScrambler(
                new ScramblerInfo("2x2x2-CUBE-RANDOM", "2x2x2-CUBE", "Random scrambler"));

        // 3x3x3 random
        Scrambler rubiksCubeRandom =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-RANDOM", "RUBIKS-CUBE", "Random scrambler"),
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });

        // 3x3x3 fridrich f2l training
        Scrambler rubiksCubeFridrichF2LTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-FRIDRICH-F2L-TRAINING", "RUBIKS-CUBE", "Fridrich - F2L training scrambler"),
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1,  4,  5,  6,  7, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1,  0,  0,  0,  0, -1, -1, -1, -1 });

        // 3x3x3 fridrich oll training
        Scrambler rubiksCubeFridrichOLLTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-FRIDRICH-OLL-TRAINING", "RUBIKS-CUBE", "Fridrich - OLL training scrambler"),
                new byte[] {  0,  1,  2,  3, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0, -1, -1, -1, -1 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0, -1, -1, -1, -1 });

        // 3x3x3 fridrich pll training
        Scrambler rubiksCubeFridrichPLLTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-FRIDRICH-PLL-TRAINING", "RUBIKS-CUBE", "Fridrich - PLL training scrambler"),
                new byte[] {  0,  1,  2,  3, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 });

        // 3x3x3 3op corners training
        Scrambler rubiksCube3OPCornersTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-CORNERS-TRAINING", "RUBIKS-CUBE", "3OP - Corners training scrambler"),
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 });

        // 3x3x3 3op corners permutation training
        Scrambler rubiksCube3OPCornersPermutationTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-CORNERS-PERMUTATION-TRAINING", "RUBIKS-CUBE", "3OP - Corners permutation training scrambler"),
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 });

        // 3x3x3 3op corners orientation training
        Scrambler rubiksCube3OPCornersOrientationTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-CORNERS-ORIENTATION-TRAINING", "RUBIKS-CUBE", "3OP - Corners orientation training scrambler"),
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 });

        // 3x3x3 3op edges training
        Scrambler rubiksCube3OPEdgesTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-EDGES-TRAINING", "RUBIKS-CUBE", "3OP - Edges training scrambler"),
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });

        // 3x3x3 3op edges permutation training
        Scrambler rubiksCube3OPEdgesPermutationTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-EDGES-PERMUTATION-TRAINING", "RUBIKS-CUBE", "3OP - Edges permutation training scrambler"),
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 });

        // 3x3x3 3op edges orientation training
        Scrambler rubiksCube3OPEdgesOrientationTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-EDGES-ORIENTATION-TRAINING", "RUBIKS-CUBE", "3OP - Edges orientation training scrambler"),
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });

        // 3x3x3 easy cross
        Scrambler rubiksCubeEasyCross =
            new RubiksCubeEasyCrossScrambler(
                new ScramblerInfo("RUBIKS-CUBE-EASY-CROSS", "RUBIKS-CUBE", "Easy cross scrambler"),
                3);

        // 4x4x4 random
        Scrambler rubiksRevengeRandom =
            new RubiksRevengeRandomScrambler(
                new ScramblerInfo("4x4x4-CUBE-RANDOM", "4x4x4-CUBE", "Random scrambler"),
                40);

        // 5x5x5 random
        Scrambler professorsCubeRandom =
            new ProfessorsCubeRandomScrambler(
                new ScramblerInfo("5x5x5-CUBE-RANDOM", "5x5x5-CUBE", "Random scrambler"),
                60);

        // 7x7x7 random
        Scrambler vCube7Random =
            new VCube7RandomScrambler(
                new ScramblerInfo("7x7x7-CUBE-RANDOM", "7x7x7-CUBE", "Random scrambler"),
                100);

        // megaminx random
        Scrambler megaminxRandom =
            new MegaminxRandomScrambler(
                new ScramblerInfo("MEGAMINX-RANDOM", "MEGAMINX", "Random scrambler"));

        // pyraminx random
        Scrambler pyraminxRandom =
            new PyraminxRandomScrambler(
                new ScramblerInfo("PYRAMINX-RANDOM", "PYRAMINX", "Random scrambler"));

        // square-1 random
        Scrambler square1Random =
            new Square1RandomScrambler(
                new ScramblerInfo("SQUARE-1-RANDOM", "SQUARE-1", "Random scrambler"),
                40);

        scramblers = new Scrambler[] {
            empty,
            rubiksPocketCubeRandom,
            rubiksCubeRandom,
            rubiksCubeFridrichF2LTraining,
            rubiksCubeFridrichOLLTraining,
            rubiksCubeFridrichPLLTraining,
            rubiksCube3OPCornersTraining,
            rubiksCube3OPCornersPermutationTraining,
            rubiksCube3OPCornersOrientationTraining,
            rubiksCube3OPEdgesTraining,
            rubiksCube3OPEdgesPermutationTraining,
            rubiksCube3OPEdgesOrientationTraining,
            rubiksCubeEasyCross,
            rubiksRevengeRandom,
            professorsCubeRandom,
            vCube7Random,
            megaminxRandom,
            pyraminxRandom,
            square1Random,
        };

        scramblerMap = new HashMap<String, Scrambler>();
        for (Scrambler scrambler : scramblers) {
            scramblerMap.put(scrambler.getScramblerInfo().getScramblerId(), scrambler);
        }
    }

    public static Scrambler getScrambler(String scramblerId) {
        return scramblerMap.get(scramblerId);
    }

    public static Scrambler[] getScramblers() {
        return scramblers;
    }
}

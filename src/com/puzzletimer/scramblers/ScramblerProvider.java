package com.puzzletimer.scramblers;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.ScramblerInfo;

public class ScramblerProvider {
    private Scrambler[] scramblers;
    private HashMap<String, Scrambler> scramblerMap;

    public ScramblerProvider() {
        // 2x2x2 importer
        Scrambler rubiksPocketCubeImporter =
            new EmptyScrambler(
                new ScramblerInfo("2x2x2-CUBE-IMPORTER", "2x2x2-CUBE", "Importer scrambler"));

        // 2x2x2 random
        Scrambler rubiksPocketCubeRandom =
            new RubiksPocketCubeRandomScrambler(
                new ScramblerInfo("2x2x2-CUBE-RANDOM", "2x2x2-CUBE", "Random scrambler"),
                0,
                new String[] { "U", "D", "L", "R", "F", "B" });

        // 2x2x2 <U, R, F>
        Scrambler rubiksPocketCubeURF =
            new RubiksPocketCubeRandomScrambler(
                new ScramblerInfo("2x2x2-CUBE-URF", "2x2x2-CUBE", "<U, R, F> scrambler"),
                0,
                new String[] { "U", "R", "F" });

        // 2x2x2 suboptimal <U, R, F>
        Scrambler rubiksPocketCubeSuboptimalURF =
            new RubiksPocketCubeRandomScrambler(
                new ScramblerInfo("2x2x2-CUBE-SUBOPTIMAL-URF", "2x2x2-CUBE", "Suboptimal <U, R, F> scrambler"),
                11,
                new String[] { "U", "R", "F" });

        // 3x3x3 importer
        Scrambler rubiksCubeImporter =
            new EmptyScrambler(
                new ScramblerInfo("RUBIKS-CUBE-IMPORTER", "RUBIKS-CUBE", "Importer scrambler"));

        // 3x3x3 random
        Scrambler rubiksCubeRandom =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-RANDOM", "RUBIKS-CUBE", "Random scrambler"),
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });

        // 3x3x3 <L, U>
        Scrambler rubiksCubeLU =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-LU", "RUBIKS-CUBE", "<L, U> scrambler"),
                new byte[] { -1, -1, -1, -1, -1,  5,  6, -1 },
                new byte[] { -1, -1, -1, -1, -1,  0,  0, -1 },
                new byte[] { -1,  1,  2, -1, -1, -1, -1, -1,  8,  9, 10, -1 },
                new byte[] { -1,  0,  0, -1, -1, -1, -1, -1,  0,  0,  0, -1 });

        // 3x3x3 <R, U>
        Scrambler rubiksCubeRU =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-RU", "RUBIKS-CUBE", "<R, U> scrambler"),
                new byte[] { -1, -1, -1, -1,  4, -1, -1,  7 },
                new byte[] { -1, -1, -1, -1,  0, -1, -1,  0 },
                new byte[] {  0, -1, -1,  3, -1, -1, -1, -1,  8, -1, 10, 11 },
                new byte[] {  0, -1, -1,  0, -1, -1, -1, -1,  0, -1,  0,  0 });

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

        // 3x3x3 3op orientation training
        Scrambler rubiksCube3OPOrientationTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-ORIENTATION-TRAINING", "RUBIKS-CUBE", "3OP - Orientation training scrambler"),
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });

        // 3x3x3 3op permutation training
        Scrambler rubiksCube3OPPermutationTraining =
            new RubiksCubeRandomScrambler(
                new ScramblerInfo("RUBIKS-CUBE-3OP-PERMUTATION-TRAINING", "RUBIKS-CUBE", "3OP - Permutation training scrambler"),
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0 },
                new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
                new byte[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 });

        // 3x3x3 easy cross
        Scrambler rubiksCubeEasyCross =
            new RubiksCubeEasyCrossScrambler(
                new ScramblerInfo("RUBIKS-CUBE-EASY-CROSS", "RUBIKS-CUBE", "Easy cross scrambler"),
                3);

        // 4x4x4 importer
        Scrambler rubiksRevengeImporter =
            new EmptyScrambler(
                new ScramblerInfo("4x4x4-CUBE-IMPORTER", "4x4x4-CUBE", "Importer scrambler"));

        // 4x4x4 random
        Scrambler rubiksRevengeRandom =
            new RubiksRevengeRandomScrambler(
                new ScramblerInfo("4x4x4-CUBE-RANDOM", "4x4x4-CUBE", "Random scrambler"),
                40);

        // 5x5x5 importer
        Scrambler professorsCubeImporter =
            new EmptyScrambler(
                new ScramblerInfo("5x5x5-CUBE-IMPORTER", "5x5x5-CUBE", "Importer scrambler"));

        // 5x5x5 random
        Scrambler professorsCubeRandom =
            new ProfessorsCubeRandomScrambler(
                new ScramblerInfo("5x5x5-CUBE-RANDOM", "5x5x5-CUBE", "Random scrambler"),
                60);

        // 6x6x6 importer
        Scrambler vCube6Importer =
            new EmptyScrambler(
                new ScramblerInfo("6x6x6-CUBE-IMPORTER", "6x6x6-CUBE", "Importer scrambler"));

        // 6x6x6 random
        Scrambler vCube6Random =
            new VCube6RandomScrambler(
                new ScramblerInfo("6x6x6-CUBE-RANDOM", "6x6x6-CUBE", "Random scrambler"),
                80);

        // 7x7x7 importer
        Scrambler vCube7Importer =
            new EmptyScrambler(
                new ScramblerInfo("7x7x7-CUBE-IMPORTER", "7x7x7-CUBE", "Importer scrambler"));

        // 7x7x7 random
        Scrambler vCube7Random =
            new VCube7RandomScrambler(
                new ScramblerInfo("7x7x7-CUBE-RANDOM", "7x7x7-CUBE", "Random scrambler"),
                100);

        // rubiks clock importer
        Scrambler rubiksClockImporter =
            new EmptyScrambler(
                new ScramblerInfo("RUBIKS-CLOCK-IMPORTER", "RUBIKS-CLOCK", "Importer scrambler"));

        // rubiks clock random
        Scrambler rubiksClockRandom =
            new RubiksClockRandomScrambler(
                new ScramblerInfo("RUBIKS-CLOCK-RANDOM", "RUBIKS-CLOCK", "Random scrambler"));

        // megaminx importer
        Scrambler megaminxImporter =
            new EmptyScrambler(
                new ScramblerInfo("MEGAMINX-IMPORTER", "MEGAMINX", "Importer scrambler"));

        // megaminx random
        Scrambler megaminxRandom =
            new MegaminxRandomScrambler(
                new ScramblerInfo("MEGAMINX-RANDOM", "MEGAMINX", "Random scrambler"));

        // pyraminx importer
        Scrambler pyraminxImporter =
            new EmptyScrambler(
                new ScramblerInfo("PYRAMINX-IMPORTER", "PYRAMINX", "Importer scrambler"));

        // pyraminx random
        Scrambler pyraminxRandom =
            new PyraminxRandomScrambler(
                new ScramblerInfo("PYRAMINX-RANDOM", "PYRAMINX", "Random scrambler"),
                0);

        // pyraminx random
        Scrambler pyraminxSuboptimalRandom =
            new PyraminxRandomScrambler(
                new ScramblerInfo("PYRAMINX-SUBOPTIMAL-RANDOM", "PYRAMINX", "Suboptimal random scrambler"),
                11);

        // pyraminx importer
        Scrambler square1Importer =
            new EmptyScrambler(
                new ScramblerInfo("SQUARE-1-IMPORTER", "SQUARE-1", "Importer scrambler"));

        // square-1 random
        Scrambler square1Random =
            new Square1RandomScrambler(
                new ScramblerInfo("SQUARE-1-RANDOM", "SQUARE-1", "Random scrambler"));

        // square-1 cube shape
        Scrambler square1CubeShape =
            new Square1CubeShapeScrambler(
                new ScramblerInfo("SQUARE-1-CUBE-SHAPE", "SQUARE-1", "Cube shape scrambler"));

        // skewb importer
        Scrambler skewbImporter =
            new EmptyScrambler(
                new ScramblerInfo("SKEWB-IMPORTER", "SKEWB", "Importer scrambler"));

        // skewb random
        Scrambler skewbRandom =
            new SkewbRandomScrambler(
                new ScramblerInfo("SKEWB-RANDOM", "SKEWB", "Random scrambler"));

        // floppy cube importer
        Scrambler floppyCubeImporter =
            new EmptyScrambler(
                new ScramblerInfo("FLOPPY-CUBE-IMPORTER", "FLOPPY-CUBE", "Importer scrambler"));

        // floppy cube random
        Scrambler floppyCubeRandom =
            new FloppyCubeRandomScrambler(
                new ScramblerInfo("FLOPPY-CUBE-RANDOM", "FLOPPY-CUBE", "Random scrambler"));

        // tower cube importer
        Scrambler towerCubeImporter =
            new EmptyScrambler(
                new ScramblerInfo("TOWER-CUBE-IMPORTER", "TOWER-CUBE", "Importer scrambler"));

        // tower cube random
        Scrambler towerCubeRandom =
            new TowerCubeRandomScrambler(
                new ScramblerInfo("TOWER-CUBE-RANDOM", "TOWER-CUBE", "Random scrambler"));

        // rubiks tower importer
        Scrambler rubiksTowerImporter =
            new EmptyScrambler(
                new ScramblerInfo("RUBIKS-TOWER-IMPORTER", "RUBIKS-TOWER", "Importer scrambler"));

        // rubiks tower random
        Scrambler rubiksTowerRandom =
            new RubiksTowerRandomScrambler(
                new ScramblerInfo("RUBIKS-TOWER-RANDOM", "RUBIKS-TOWER", "Random scrambler"));

        // rubik's domino importer
        Scrambler rubiksDominoImporter =
            new EmptyScrambler(
                new ScramblerInfo("RUBIKS-DOMINO-IMPORTER", "RUBIKS-DOMINO", "Importer scrambler"));

        // rubik's domino random
        Scrambler rubiksDominoRandom =
            new RubiksDominoRandomScrambler(
                new ScramblerInfo("RUBIKS-DOMINO-RANDOM", "RUBIKS-DOMINO", "Random scrambler"));

        // other importer
        Scrambler otherImporter =
            new EmptyScrambler(
                new ScramblerInfo("OTHER-IMPORTER", "OTHER", "Importer scrambler"));

        // empty
        Scrambler empty =
            new EmptyScrambler(
                new ScramblerInfo("EMPTY", "OTHER", "Empty scrambler"));

        this.scramblers = new Scrambler[] {
            rubiksPocketCubeImporter,
            rubiksPocketCubeRandom,
            rubiksPocketCubeURF,
            rubiksPocketCubeSuboptimalURF,
            rubiksCubeImporter,
            rubiksCubeRandom,
            rubiksCubeLU,
            rubiksCubeRU,
            rubiksCubeFridrichF2LTraining,
            rubiksCubeFridrichOLLTraining,
            rubiksCubeFridrichPLLTraining,
            rubiksCube3OPCornersTraining,
            rubiksCube3OPCornersPermutationTraining,
            rubiksCube3OPCornersOrientationTraining,
            rubiksCube3OPEdgesTraining,
            rubiksCube3OPEdgesPermutationTraining,
            rubiksCube3OPEdgesOrientationTraining,
            rubiksCube3OPOrientationTraining,
            rubiksCube3OPPermutationTraining,
            rubiksCubeEasyCross,
            rubiksRevengeImporter,
            rubiksRevengeRandom,
            professorsCubeImporter,
            professorsCubeRandom,
            vCube6Importer,
            vCube6Random,
            vCube7Importer,
            vCube7Random,
            rubiksClockImporter,
            rubiksClockRandom,
            megaminxImporter,
            megaminxRandom,
            pyraminxImporter,
            pyraminxRandom,
            pyraminxSuboptimalRandom,
            skewbImporter,
            skewbRandom,
            square1Importer,
            square1Random,
            square1CubeShape,
            floppyCubeImporter,
            floppyCubeRandom,
            towerCubeImporter,
            towerCubeRandom,
            rubiksTowerImporter,
            rubiksTowerRandom,
            rubiksDominoImporter,
            rubiksDominoRandom,
            otherImporter,
            empty,
        };

        this.scramblerMap = new HashMap<String, Scrambler>();
        for (Scrambler scrambler : this.scramblers) {
            this.scramblerMap.put(scrambler.getScramblerInfo().getScramblerId(), scrambler);
        }
    }

    public Scrambler[] getAll() {
        ArrayList<Scrambler> scramblers = new ArrayList<Scrambler>();
        for (Scrambler scrambler : this.scramblers) {
            if (!scrambler.getScramblerInfo().getScramblerId().endsWith("-IMPORTER")) {
                scramblers.add(scrambler);
            }
        }

        Scrambler[] scramblersArray = new Scrambler[scramblers.size()];
        scramblers.toArray(scramblersArray);

        return scramblersArray;
    }

    public Scrambler get(String scramblerId) {
        return this.scramblerMap.get(scramblerId);
    }
}

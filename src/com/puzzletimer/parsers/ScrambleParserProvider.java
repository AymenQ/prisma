package com.puzzletimer.parsers;

import java.util.HashMap;

public class ScrambleParserProvider {
    private ScrambleParser[] scrambleParsers;
    private HashMap<String, ScrambleParser> scrambleParserMap;

    public ScrambleParserProvider() {
        this.scrambleParsers = new ScrambleParser[] {
            new EmptyScrambleParser(),
            new FloppyCubeScrambleParser(),
            new MegaminxScrambleParser(),
            new ProfessorsCubeScrambleParser(),
            new PyraminxScrambleParser(),
            new RubiksClockScrambleParser(),
            new RubiksCubeScrambleParser(),
            new RubiksDominoScrambleParser(),
            new RubiksPocketCubeScrambleParser(),
            new RubiksRevengeScramblerParser(),
            new RubiksTowerScramblerParser(),
            new SkewbScrambleParser(),
            new Square1ScrambleParser(),
            new TowerCubeScrambleParser(),
            new VCube6ScrambleParser(),
            new VCube7ScrambleParser(),
            new SS9ScrambleParser(),
            new SS8ScrambleParser(),
        };

        this.scrambleParserMap = new HashMap<String, ScrambleParser>();
        for (ScrambleParser scrambleParser : this.scrambleParsers) {
            this.scrambleParserMap.put(scrambleParser.getPuzzleId(), scrambleParser);
        }
    }

    public ScrambleParser get(String puzzleId) {
        return this.scrambleParserMap.get(puzzleId);
    }
}

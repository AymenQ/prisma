package com.puzzletimer.parsers;

import java.util.HashMap;

public class ScrambleParserProvider {
    private ScrambleParser[] scrambleParsers;
    private HashMap<String, ScrambleParser> scrambleParserMap;

    public ScrambleParserProvider() {
        this.scrambleParsers = new ScrambleParser[] {
            new EmptyScrambleParser(),
            new MegaminxScrambleParser(),
            new ProfessorsCubeScrambleParser(),
            new PyraminxScrambleParser(),
            new RubiksClockScrambleParser(),
            new RubiksCubeScrambleParser(),
            new RubiksPocketCubeScrambleParser(),
            new RubiksRevengeScramblerParser(),
            new Square1ScrambleParser(),
            new VCube6ScrambleParser(),
            new VCube7ScrambleParser(),
            new FloppyCubeScrambleParser(),
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

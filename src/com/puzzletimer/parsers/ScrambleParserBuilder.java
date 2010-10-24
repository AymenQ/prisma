package com.puzzletimer.parsers;

import java.util.HashMap;

public class ScrambleParserBuilder {
    private static ScrambleParser[] scrambleParsers;
    private static HashMap<String, ScrambleParser> scrambleParserMap;

    static {
        scrambleParsers = new ScrambleParser[] {
            new EmptyScrambleParser(),
            new MegaminxScrambleParser(),
            new ProfessorsCubeScrambleParser(),
            new PyraminxScrambleParser(),
            new RubiksCubeScrambleParser(),
            new RubiksPocketCubeScrambleParser(),
            new RubiksRevengeScramblerParser(),
            new Square1ScrambleParser(),
        };

        scrambleParserMap = new HashMap<String, ScrambleParser>();
        for (ScrambleParser scrambleParser : scrambleParsers) {
            scrambleParserMap.put(scrambleParser.getPuzzleId(), scrambleParser);
        }
    }

    public static ScrambleParser getScrambleParser(String puzzleId) {
        return scrambleParserMap.get(puzzleId);
    }
}

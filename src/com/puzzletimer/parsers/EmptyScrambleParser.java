package com.puzzletimer.parsers;

public class EmptyScrambleParser implements ScrambleParser {
    @Override
    public String getPuzzleId() {
        return "OTHER";
    }

    @Override
    public String[] parse(String input) {
        return new String[0];
    }
}

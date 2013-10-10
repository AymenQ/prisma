package com.puzzletimer.parsers;

public interface ScrambleParser {
    String getPuzzleId();
    String[] parse(String input);
}

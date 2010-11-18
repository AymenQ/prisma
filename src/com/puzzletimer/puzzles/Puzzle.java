package com.puzzletimer.puzzles;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public interface Puzzle {
    PuzzleInfo getPuzzleInfo();
    Mesh getScrambledPuzzleMesh(ColorScheme colorScheme, String[] sequence);
}

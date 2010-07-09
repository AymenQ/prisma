package com.puzzletimer.puzzles;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.models.PuzzleInfo;
import com.puzzletimer.models.Scramble;

public interface Puzzle extends PuzzleInfo {
    Mesh getScrambledPuzzleMesh(Scramble scramble);
}

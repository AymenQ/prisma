package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.models.PuzzleInfo;

public interface Puzzle {
    PuzzleInfo getPuzzleInfo();
    Mesh getScrambledPuzzleMesh(HashMap<String, Color> colors, String[] sequence);
}

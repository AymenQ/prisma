package com.puzzletimer.puzzles;

import java.util.ArrayList;

import com.puzzletimer.graphics.Face;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.algebra.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public class Other implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("OTHER", "Other");
    }

    @Override
    public String toString() {
        return getPuzzleInfo().getDescription();
    }

    @Override
    public Mesh getScrambledPuzzleMesh(ColorScheme colorScheme, String[] sequence) {
        return new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());
    }
}

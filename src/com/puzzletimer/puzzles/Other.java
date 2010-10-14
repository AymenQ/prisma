package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.graphics.Face;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.algebra.Vector3;
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
    public Mesh getScrambledPuzzleMesh(HashMap<String, Color> colors, String[] sequence) {
        return new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());
    }
}

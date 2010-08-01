package com.puzzletimer.puzzles;

import java.util.ArrayList;

import com.puzzletimer.graphics.Face;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.models.Scramble;

public class Empty implements Puzzle {
    @Override
    public String getPuzzleId() {
        return "EMPTY";
    }

    @Override
    public String getDescription() {
        return "Empty";
    }

    @Override
    public Mesh getScrambledPuzzleMesh(Scramble scramble) {
        return new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());
    }
}

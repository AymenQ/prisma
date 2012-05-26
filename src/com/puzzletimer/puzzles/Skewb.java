package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Matrix44;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.Plane;
import com.puzzletimer.graphics.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public class Skewb implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("SKEWB");
    }

    @Override
    public String toString() {
        return getPuzzleInfo().getDescription();
    }

    private class Twist {
        public Plane plane;
        public double angle;

        public Twist(Plane plane, double angle) {
            this.plane = plane;
            this.angle = angle;
        }
    }

    @Override
    public Mesh getScrambledPuzzleMesh(ColorScheme colorScheme, String[] sequence) {
        Color[] colorArray = {
            colorScheme.getFaceColor("FACE-L").getColor(),
            colorScheme.getFaceColor("FACE-B").getColor(),
            colorScheme.getFaceColor("FACE-D").getColor(),
            colorScheme.getFaceColor("FACE-R").getColor(),
            colorScheme.getFaceColor("FACE-F").getColor(),
            colorScheme.getFaceColor("FACE-U").getColor(),
        };

        Mesh mesh = Mesh.cube(colorArray);

        Plane planeL = new Plane(
            new Vector3( 0.5,  0.5,    0),
            new Vector3( 0.5,    0, -0.5),
            new Vector3(   0, -0.5, -0.5));
        Plane planeR = new Plane(
            new Vector3( 0.5,    0, -0.5),
            new Vector3(   0,  0.5, -0.5),
            new Vector3(-0.5,  0.5,    0));
        Plane planeD = new Plane(
            new Vector3(   0,  0.5, -0.5),
            new Vector3( 0.5,  0.5,    0),
            new Vector3( 0.5,    0,  0.5));
        Plane planeB = new Plane(
            new Vector3( 0.5,    0,  0.5),
            new Vector3(   0,  0.5,  0.5),
            new Vector3(-0.5,  0.5,    0));

        mesh = mesh
            .cut(planeL, 0)
            .cut(planeR, 0)
            .cut(planeD, 0)
            .cut(planeB, 0)
            .shortenFaces(0.05)
            .softenFaces(0.02)
            .softenFaces(0.01);

        HashMap<String, Twist> twists = new HashMap<String, Twist>();
        twists.put("L",  new Twist(planeL,  2 * Math.PI / 3));
        twists.put("L'", new Twist(planeL, -2 * Math.PI / 3));
        twists.put("R",  new Twist(planeR,  2 * Math.PI / 3));
        twists.put("R'", new Twist(planeR, -2 * Math.PI / 3));
        twists.put("D",  new Twist(planeD,  2 * Math.PI / 3));
        twists.put("D'", new Twist(planeD, -2 * Math.PI / 3));
        twists.put("B",  new Twist(planeB,  2 * Math.PI / 3));
        twists.put("B'", new Twist(planeB, -2 * Math.PI / 3));

        for (String move : sequence) {
            Twist t = twists.get(move);
            mesh = mesh.rotateHalfspace(t.plane, t.angle);
        }

        return mesh
            .transform(Matrix44.rotationY(-Math.PI / 6))
            .transform(Matrix44.rotationX(Math.PI / 7));
    }
}

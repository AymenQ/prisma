package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.algebra.Matrix44;
import com.puzzletimer.graphics.algebra.Vector3;
import com.puzzletimer.graphics.geometry.Plane;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public class RubiksPocketCube implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("2x2x2-CUBE");
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

        Plane planeL = new Plane(new Vector3( -0.0, 0, 0), new Vector3(-1, 0, 0));
        Plane planeR = new Plane(new Vector3(  0.0, 0, 0), new Vector3( 1, 0, 0));
        Plane planeD = new Plane(new Vector3(0,  -0.0, 0), new Vector3(0, -1, 0));
        Plane planeU = new Plane(new Vector3(0,   0.0, 0), new Vector3(0,  1, 0));
        Plane planeF = new Plane(new Vector3(0, 0,  -0.0), new Vector3(0, 0, -1));
        Plane planeB = new Plane(new Vector3(0, 0,   0.0), new Vector3(0, 0,  1));

        mesh = mesh
            .cut(planeR, 0)
            .cut(planeU, 0)
            .cut(planeF, 0)
            .shortenFaces(0.04)
            .softenFaces(0.02)
            .softenFaces(0.01);

        HashMap<String, Twist> twists = new HashMap<String, Twist>();
        twists.put("L",   new Twist(planeL,   Math.PI / 2));
        twists.put("L2",  new Twist(planeL,   Math.PI));
        twists.put("L'",  new Twist(planeL,  -Math.PI / 2));
        twists.put("R",   new Twist(planeR,   Math.PI / 2));
        twists.put("R2",  new Twist(planeR,   Math.PI));
        twists.put("R'",  new Twist(planeR,  -Math.PI / 2));
        twists.put("D",   new Twist(planeD,   Math.PI / 2));
        twists.put("D2",  new Twist(planeD,   Math.PI));
        twists.put("D'",  new Twist(planeD,  -Math.PI / 2));
        twists.put("U",   new Twist(planeU,   Math.PI / 2));
        twists.put("U2",  new Twist(planeU,   Math.PI));
        twists.put("U'",  new Twist(planeU,  -Math.PI / 2));
        twists.put("F",   new Twist(planeF,   Math.PI / 2));
        twists.put("F2",  new Twist(planeF,   Math.PI));
        twists.put("F'",  new Twist(planeF,  -Math.PI / 2));
        twists.put("B",   new Twist(planeB,   Math.PI / 2));
        twists.put("B2",  new Twist(planeB,   Math.PI));
        twists.put("B'",  new Twist(planeB,  -Math.PI / 2));

        for (String move : sequence) {
            Twist t = twists.get(move);
            mesh = mesh.transformHalfspace(
                Matrix44.rotation(t.plane.n, t.angle),
                t.plane);
        }

        return mesh
            .transform(Matrix44.rotationY(-Math.PI / 6))
            .transform(Matrix44.rotationX(Math.PI / 7));
    }
}

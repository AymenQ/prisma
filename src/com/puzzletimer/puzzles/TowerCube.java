package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Matrix44;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.Plane;
import com.puzzletimer.graphics.Vector3;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public class TowerCube implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("TOWER-CUBE");
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

        Mesh mesh = Mesh.cube(colorArray)
            .transform(
                new Matrix44(new double[][] {
                    { 2d / 3, 0,      0, 0 },
                    { 0,      1,      0, 0 },
                    { 0,      0, 2d / 3, 0 },
                    { 0,      0,      0, 1 },
                }));

        Plane planeL = new Plane(new Vector3(0,      0, 0), new Vector3(-1, 0, 0));
        Plane planeR = new Plane(new Vector3(0,      0, 0), new Vector3( 1, 0, 0));
        Plane planeD = new Plane(new Vector3(0, -0.166, 0), new Vector3(0, -1, 0));
        Plane planeU = new Plane(new Vector3(0,  0.166, 0), new Vector3(0,  1, 0));
        Plane planeF = new Plane(new Vector3(0, 0,     -0), new Vector3(0, 0, -1));
        Plane planeB = new Plane(new Vector3(0, 0,      0), new Vector3(0, 0,  1));

        mesh = mesh
            .cut(planeL, 0)
            .cut(planeD, 0)
            .cut(planeU, 0)
            .cut(planeF, 0)
            .shortenFaces(0.03)
            .softenFaces(0.015)
            .softenFaces(0.005);

        HashMap<String, Twist> twists = new HashMap<String, Twist>();
        twists.put("L",  new Twist(planeL,  Math.PI));
        twists.put("R",  new Twist(planeR,  Math.PI));
        twists.put("D",  new Twist(planeD,  Math.PI / 2));
        twists.put("D2", new Twist(planeD,  Math.PI));
        twists.put("D'", new Twist(planeD, -Math.PI / 2));
        twists.put("U",  new Twist(planeU,  Math.PI / 2));
        twists.put("U2", new Twist(planeU,  Math.PI));
        twists.put("U'", new Twist(planeU, -Math.PI / 2));
        twists.put("F",  new Twist(planeF,  Math.PI));
        twists.put("B",  new Twist(planeB,  Math.PI));


        for (String move : sequence) {
            Twist t = twists.get(move);
            mesh = mesh.rotateHalfspace(t.plane, t.angle);
        }

        return mesh
            .transform(
                new Matrix44(new double[][] {
                    { 1.2,   0,   0, 0 },
                    {   0, 1.2,   0, 0 },
                    {   0,   0, 1.2, 0 },
                    {   0,   0,   0, 1 },
                }))
            .transform(Matrix44.rotationY(-Math.PI / 6))
            .transform(Matrix44.rotationX(Math.PI / 7));
    }
}

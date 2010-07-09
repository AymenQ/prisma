package com.puzzletimer.puzzles;

import java.util.HashMap;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.HSLColor;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.models.Scramble;

public class RubiksRevenge implements Puzzle {
    @Override
    public String getPuzzleId() {
        return "4x4x4-CUBE";
    }

    @Override
    public String getDescription() {
        return "4x4x4 cube";
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
    public Mesh getScrambledPuzzleMesh(Scramble scramble) {
        HSLColor[] colors = {
            new HSLColor( 20, 100,  50), // L - orange
            new HSLColor(235, 100,  30), // B - blue
            new HSLColor( 55, 100,  50), // D - yellow
            new HSLColor(  0,  85,  45), // R - red
            new HSLColor(120, 100,  30), // F - green
            new HSLColor(  0,   0, 100), // U - white
        };

        Mesh mesh = Mesh.cube(colors);

        Plane planeL  = new Plane(new Vector3(-0.25, 0, 0), new Vector3(-1, 0, 0));
        Plane planeLw = new Plane(new Vector3( -0.0, 0, 0), new Vector3(-1, 0, 0));
        Plane planeR  = new Plane(new Vector3( 0.25, 0, 0), new Vector3( 1, 0, 0));
        Plane planeRw = new Plane(new Vector3(  0.0, 0, 0), new Vector3( 1, 0, 0));
        Plane planeD  = new Plane(new Vector3(0, -0.25, 0), new Vector3(0, -1, 0));
        Plane planeDw = new Plane(new Vector3(0,  -0.0, 0), new Vector3(0, -1, 0));
        Plane planeU  = new Plane(new Vector3(0,  0.25, 0), new Vector3(0,  1, 0));
        Plane planeUw = new Plane(new Vector3(0,   0.0, 0), new Vector3(0,  1, 0));
        Plane planeF  = new Plane(new Vector3(0, 0, -0.25), new Vector3(0, 0, -1));
        Plane planeFw = new Plane(new Vector3(0, 0,  -0.0), new Vector3(0, 0, -1));
        Plane planeB  = new Plane(new Vector3(0, 0,  0.25), new Vector3(0, 0,  1));
        Plane planeBw = new Plane(new Vector3(0, 0,   0.0), new Vector3(0, 0,  1));

        mesh = mesh
            .cut(planeL,  0)
            .cut(planeR,  0)
            .cut(planeLw, 0)
            .cut(planeD,  0)
            .cut(planeU,  0)
            .cut(planeDw, 0)
            .cut(planeF,  0)
            .cut(planeB,  0)
            .cut(planeFw, 0)
            .shortenFaces(0.025)
            .softenFaces(0.015)
            .softenFaces(0.005);

        HashMap<String, Twist> twists = new HashMap<String, Twist>();
        twists.put("L",   new Twist(planeL,   Math.PI / 2));
        twists.put("Lw",  new Twist(planeLw,  Math.PI / 2));
        twists.put("L2",  new Twist(planeL,   Math.PI));
        twists.put("Lw2", new Twist(planeLw,  Math.PI));
        twists.put("L'",  new Twist(planeL,  -Math.PI / 2));
        twists.put("Lw'", new Twist(planeLw, -Math.PI / 2));
        twists.put("R",   new Twist(planeR,   Math.PI / 2));
        twists.put("Rw",  new Twist(planeRw,  Math.PI / 2));
        twists.put("R2",  new Twist(planeR,   Math.PI));
        twists.put("Rw2", new Twist(planeRw,  Math.PI));
        twists.put("R'",  new Twist(planeR,  -Math.PI / 2));
        twists.put("Rw'", new Twist(planeRw, -Math.PI / 2));
        twists.put("D",   new Twist(planeD,   Math.PI / 2));
        twists.put("Dw",  new Twist(planeDw,  Math.PI / 2));
        twists.put("D2",  new Twist(planeD,   Math.PI));
        twists.put("Dw2", new Twist(planeDw,  Math.PI));
        twists.put("D'",  new Twist(planeD,  -Math.PI / 2));
        twists.put("Dw'", new Twist(planeDw, -Math.PI / 2));
        twists.put("U",   new Twist(planeU,   Math.PI / 2));
        twists.put("Uw",  new Twist(planeUw,  Math.PI / 2));
        twists.put("U2",  new Twist(planeU,   Math.PI));
        twists.put("Uw2", new Twist(planeUw,  Math.PI));
        twists.put("U'",  new Twist(planeU,  -Math.PI / 2));
        twists.put("Uw'", new Twist(planeUw, -Math.PI / 2));
        twists.put("F",   new Twist(planeF,   Math.PI / 2));
        twists.put("Fw",  new Twist(planeFw,  Math.PI / 2));
        twists.put("F2",  new Twist(planeF,   Math.PI));
        twists.put("Fw2", new Twist(planeFw,  Math.PI));
        twists.put("F'",  new Twist(planeF,  -Math.PI / 2));
        twists.put("Fw'", new Twist(planeFw, -Math.PI / 2));
        twists.put("B",   new Twist(planeB,   Math.PI / 2));
        twists.put("Bw",  new Twist(planeBw,  Math.PI / 2));
        twists.put("B2",  new Twist(planeB,   Math.PI));
        twists.put("Bw2", new Twist(planeBw,  Math.PI));
        twists.put("B'",  new Twist(planeB,  -Math.PI / 2));
        twists.put("Bw'", new Twist(planeBw, -Math.PI / 2));

        for (String move : scramble.getSequence()) {
            Twist t = twists.get(move);
            mesh = mesh.transformHalfspace(
                Matrix33.rotation(t.plane.n, t.angle),
                t.plane);
        }

        return mesh
            .transform(Matrix33.rotationY(-Math.PI / 6))
            .transform(Matrix33.rotationX(Math.PI / 7));
    }
}

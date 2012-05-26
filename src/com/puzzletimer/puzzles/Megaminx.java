package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Matrix44;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.Plane;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public class Megaminx implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("MEGAMINX");
    }

    @Override
    public String toString() {
        return getPuzzleInfo().getDescription();
    }

    private static class Twist {
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
            colorScheme.getFaceColor("FACE-1").getColor(),
            colorScheme.getFaceColor("FACE-2").getColor(),
            colorScheme.getFaceColor("FACE-3").getColor(),
            colorScheme.getFaceColor("FACE-4").getColor(),
            colorScheme.getFaceColor("FACE-5").getColor(),
            colorScheme.getFaceColor("FACE-6").getColor(),
            colorScheme.getFaceColor("FACE-7").getColor(),
            colorScheme.getFaceColor("FACE-8").getColor(),
            colorScheme.getFaceColor("FACE-9").getColor(),
            colorScheme.getFaceColor("FACE-10").getColor(),
            colorScheme.getFaceColor("FACE-11").getColor(),
            colorScheme.getFaceColor("FACE-12").getColor(),
        };

        Mesh mesh = Mesh.dodecahedron(colorArray)
            .shortenFaces(0.025);

        Plane[] planes = new Plane[mesh.faces.length];
        for (int i = 0; i < planes.length; i++) {
            Plane p = new Plane(
                mesh.faces[i].vertices[0],
                mesh.faces[i].vertices[1],
                mesh.faces[i].vertices[2]);
            planes[i] = new Plane(p.p.sub(p.n.mul(0.25)), p.n);
        }

        for (Plane plane : planes) {
            mesh = mesh.cut(plane, 0.03);
        }

        mesh = mesh
            .softenFaces(0.01)
            .softenFaces(0.005);

        Plane planeR = new Plane(planes[3].p, planes[3].n.neg());
        Plane planeD = new Plane(planes[7].p, planes[7].n.neg());
        Plane planeU = planes[7];

        HashMap<String, Twist> twists = new HashMap<String, Twist>();
        twists.put("R++", new Twist(planeR,  4 * Math.PI / 5));
        twists.put("R--", new Twist(planeR, -4 * Math.PI / 5));
        twists.put("D++", new Twist(planeD,  4 * Math.PI / 5));
        twists.put("D--", new Twist(planeD, -4 * Math.PI / 5));
        twists.put("U",   new Twist(planeU,  2 * Math.PI / 5));
        twists.put("U'",  new Twist(planeU, -2 * Math.PI / 5));

        for (String move : sequence) {
            Twist t = twists.get(move);
            mesh = mesh.rotateHalfspace(t.plane, t.angle);
        }

        return mesh
            .transform(Matrix44.rotationY(Math.PI / 16));
    }
}

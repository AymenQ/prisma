package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Matrix44;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.Plane;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.PuzzleInfo;

public class Pyraminx implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("PYRAMINX");
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
            colorScheme.getFaceColor("FACE-D").getColor(),
            colorScheme.getFaceColor("FACE-L").getColor(),
            colorScheme.getFaceColor("FACE-R").getColor(),
            colorScheme.getFaceColor("FACE-F").getColor(),
        };

        Mesh mesh = Mesh.tetrahedron(colorArray);

        double h1 = Math.sqrt(8d) / 3d * (Math.sqrt(3d) / 2d * 1.5d);

        Plane plane1 = new Plane(
            mesh.faces[0].vertices[0],
            mesh.faces[0].vertices[1],
            mesh.faces[0].vertices[2]);

        Plane planeu = new Plane(plane1.p.sub(plane1.n.mul(2d * h1 / 3d)), plane1.n.neg());
        Plane planeU = new Plane(plane1.p.sub(plane1.n.mul(h1 / 3d)), plane1.n.neg());

        Plane plane2 = new Plane(
            mesh.faces[1].vertices[0],
            mesh.faces[1].vertices[1],
            mesh.faces[1].vertices[2]);

        Plane planer = new Plane(plane2.p.sub(plane2.n.mul(2d * h1 / 3d)), plane2.n.neg());
        Plane planeR = new Plane(plane2.p.sub(plane2.n.mul(h1 / 3d)), plane2.n.neg());

        Plane plane3 = new Plane(
            mesh.faces[2].vertices[0],
            mesh.faces[2].vertices[1],
            mesh.faces[2].vertices[2]);

        Plane planel = new Plane(plane3.p.sub(plane3.n.mul(2d * h1 / 3d)), plane3.n.neg());
        Plane planeL = new Plane(plane3.p.sub(plane3.n.mul(h1 / 3d)), plane3.n.neg());

        Plane plane4 = new Plane(
            mesh.faces[3].vertices[0],
            mesh.faces[3].vertices[1],
            mesh.faces[3].vertices[2]);

        Plane planeb = new Plane(plane4.p.sub(plane4.n.mul(2d * h1 / 3d)), plane4.n.neg());
        Plane planeB = new Plane(plane4.p.sub(plane4.n.mul(h1 / 3d)), plane4.n.neg());

        mesh = mesh
            .cut(planeu, 0)
            .cut(planeU, 0)
            .cut(planer, 0)
            .cut(planeR, 0)
            .cut(planel, 0)
            .cut(planeL, 0)
            .cut(planeb, 0)
            .cut(planeB, 0)
            .shortenFaces(0.05)
            .softenFaces(0.02)
            .softenFaces(0.01);

        HashMap<String, Twist> twists = new HashMap<String, Twist>();
        twists.put("U",  new Twist(planeU,  2 * Math.PI / 3));
        twists.put("U'", new Twist(planeU, -2 * Math.PI / 3));
        twists.put("u",  new Twist(planeu,  2 * Math.PI / 3));
        twists.put("u'", new Twist(planeu, -2 * Math.PI / 3));
        twists.put("L",  new Twist(planeL,  2 * Math.PI / 3));
        twists.put("L'", new Twist(planeL, -2 * Math.PI / 3));
        twists.put("l",  new Twist(planel,  2 * Math.PI / 3));
        twists.put("l'", new Twist(planel, -2 * Math.PI / 3));
        twists.put("R",  new Twist(planeR,  2 * Math.PI / 3));
        twists.put("R'", new Twist(planeR, -2 * Math.PI / 3));
        twists.put("r",  new Twist(planer,  2 * Math.PI / 3));
        twists.put("r'", new Twist(planer, -2 * Math.PI / 3));
        twists.put("B",  new Twist(planeB,  2 * Math.PI / 3));
        twists.put("B'", new Twist(planeB, -2 * Math.PI / 3));
        twists.put("b",  new Twist(planeb,  2 * Math.PI / 3));
        twists.put("b'", new Twist(planeb, -2 * Math.PI / 3));

        for (String move : sequence) {
            Twist t = twists.get(move);
            mesh = mesh.rotateHalfspace(t.plane, t.angle);
        }

        return mesh
            .transform(Matrix44.rotationY(-Math.PI / 4))
            .transform(Matrix44.rotationX(Math.PI / 6));
    }
}

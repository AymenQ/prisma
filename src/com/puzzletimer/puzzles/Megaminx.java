package com.puzzletimer.puzzles;

import java.awt.Color;
import java.util.HashMap;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.algebra.Matrix33;
import com.puzzletimer.graphics.geometry.Plane;
import com.puzzletimer.models.PuzzleInfo;

public class Megaminx implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("MEGAMINX", "Megaminx");
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
    public Mesh getScrambledPuzzleMesh(HashMap<String, Color> colors, String[] sequence) {
        Color[] colorArray = {
            colors.get("Face 01"),
            colors.get("Face 02"),
            colors.get("Face 03"),
            colors.get("Face 04"),
            colors.get("Face 05"),
            colors.get("Face 06"),
            colors.get("Face 07"),
            colors.get("Face 08"),
            colors.get("Face 09"),
            colors.get("Face 10"),
            colors.get("Face 11"),
            colors.get("Face 12"),
        };

        Mesh mesh = Mesh.dodecahedron(colorArray)
            .shortenFaces(0.025);

        Plane[] planes = new Plane[mesh.faces.size()];
        for (int i = 0; i < mesh.faces.size(); i++) {
            Plane p = Plane.fromVectors(
                    mesh.vertices.get(mesh.faces.get(i).vertexIndices.get(0)),
                    mesh.vertices.get(mesh.faces.get(i).vertexIndices.get(1)),
                    mesh.vertices.get(mesh.faces.get(i).vertexIndices.get(2)));
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
            mesh = mesh.transformHalfspace(
                Matrix33.rotation(t.plane.n, t.angle),
                t.plane);
        }

        return mesh
            .transform(Matrix33.rotationY(Math.PI / 16));
    }
}

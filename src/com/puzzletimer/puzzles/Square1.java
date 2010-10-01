package com.puzzletimer.puzzles;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.puzzletimer.graphics.HSLColor;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.graphics.algebra.Matrix33;
import com.puzzletimer.graphics.algebra.Vector3;
import com.puzzletimer.graphics.geometry.Plane;
import com.puzzletimer.models.PuzzleInfo;

public class Square1 implements Puzzle {
    @Override
    public PuzzleInfo getPuzzleInfo() {
        return new PuzzleInfo("SQUARE-1", "Square-1");
    }

    @Override
    public Mesh getScrambledPuzzleMesh(String[] sequence) {
        HSLColor[] colors = {
            new HSLColor( 55, 100,  50), // L - yellow
            new HSLColor(  0,  85,  45), // B - red
            new HSLColor(120,  65,  40), // D - green
            new HSLColor(235, 100,  30), // R - blue
            new HSLColor( 25, 100,  50), // F - orange
            new HSLColor(  0,   0, 100), // U - white
        };

        Mesh cube = Mesh.cube(colors);

        Plane planeD = new Plane(
            new Vector3(0, -0.166, 0),
            new Vector3(0, -1, 0));

        Plane planeU = new Plane(
            new Vector3(0,  0.166, 0),
            new Vector3(0,  1, 0));

        Plane planeR = new Plane(
            new Vector3(0, 0, 0),
            Matrix33.rotationY(-Math.PI / 12).mul(new Vector3(1, 0, 0)));

        Plane p1 = new Plane(
            new Vector3(0, 0, 0),
            Matrix33.rotationY(Math.PI / 12).mul(new Vector3(1, 0, 0)));

        Plane p2 = new Plane(
            new Vector3(0, 0, 0),
            Matrix33.rotationY(-Math.PI / 12).mul(new Vector3(0, 0, 1)));

        Plane p3 = new Plane(
            new Vector3(0, 0, 0),
            Matrix33.rotationY(Math.PI / 12).mul(new Vector3(0, 0, 1)));


        Mesh mesh = cube
            .cut(planeD, 0.01)
            .cut(planeU, 0.01)
            .cut(planeR, 0.01)
            .cut(p1, 0.01)
            .cut(p2, 0.01)
            .cut(p3, 0.01)
            .shortenFaces(0.02)
            .softenFaces(0.015)
            .softenFaces(0.005);

        Mesh topLayer = mesh.clip(planeU);
        Mesh bottomLayer = mesh.clip(planeD);


        Mesh bandagedMesh = cube
            .cut(planeD, 0.01)
            .cut(planeU, 0.01)
            .cut(planeR, 0.01)
            .shortenFaces(0.02)
            .softenFaces(0.015)
            .softenFaces(0.005);

        Mesh middleLayer = bandagedMesh
            .clip(new Plane(planeU.p, planeU.n.neg()))
            .clip(new Plane(planeD.p, planeD.n.neg()));


        cube = topLayer.union(middleLayer).union(bottomLayer);


        Pattern p = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");
        for (String m : sequence) {
            Matcher matcher = p.matcher(m.toString());
            matcher.find();

            int top = Integer.parseInt(matcher.group(1));
            cube = cube.transformHalfspace(
                Matrix33.rotation(planeU.n, top * Math.PI / 6),
                planeU);

            int bottom = Integer.parseInt(matcher.group(2));
            cube = cube.transformHalfspace(
                    Matrix33.rotation(planeD.n, bottom * Math.PI / 6),
                    planeD);

            cube = cube.transformHalfspace(
                    Matrix33.rotation(planeR.n, Math.PI),
                    planeR);
        }

        return cube
            .transform(Matrix33.rotationY(-Math.PI / 6))
            .transform(Matrix33.rotationX(Math.PI / 7));
    }
}

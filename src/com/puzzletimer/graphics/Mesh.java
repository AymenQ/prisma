package com.puzzletimer.graphics;

import java.util.ArrayList;

import com.puzzletimer.graphics.algebra.Matrix33;
import com.puzzletimer.graphics.algebra.Vector3;
import com.puzzletimer.graphics.geometry.Intersection;
import com.puzzletimer.graphics.geometry.LineSegment;
import com.puzzletimer.graphics.geometry.Plane;

public class Mesh {
    public ArrayList<Vector3> vertices;
    public ArrayList<Face> faces;

    public Mesh(ArrayList<Vector3> vertices, ArrayList<Face> faces) {
        this.vertices = vertices;
        this.faces = faces;
    }

    public Mesh transform(Matrix33 m) {
        Mesh mesh = new Mesh(new ArrayList<Vector3>(), faces);

        for (Vector3 v : vertices) {
            mesh.vertices.add(m.mul(v));
        }

        return mesh;
    }

    public Mesh transformHalfspace(Matrix33 m, Plane p) {
        Mesh mesh = new Mesh(new ArrayList<Vector3>(), faces);

        for (Vector3 v : vertices) {
            if (Intersection.pointHalfspace(v, p)) {
                mesh.vertices.add(m.mul(v));
            } else {
                mesh.vertices.add(v);
            }
        }

        return mesh;
    }

    public Mesh union(Mesh m) {
        Mesh mesh = new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());

        for (Face face : faces) {
            Face f = new Face(new ArrayList<Integer>(), face.color);

            for (int i = 0; i < face.vertexIndices.size(); i++)
            {
                mesh.vertices.add(vertices.get(face.vertexIndices.get(i)));
                f.vertexIndices.add(mesh.vertices.size() - 1);
            }

            mesh.faces.add(f);
        }

        for (Face face : m.faces) {
            Face f = new Face(new ArrayList<Integer>(), face.color);

            for (int i = 0; i < face.vertexIndices.size(); i++)
            {
                mesh.vertices.add(m.vertices.get(face.vertexIndices.get(i)));
                f.vertexIndices.add(mesh.vertices.size() - 1);
            }

            mesh.faces.add(f);
        }

        return mesh;
    }

    public Mesh clip(Plane p) {
        Mesh mesh = new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());

        for (Face face : faces) {
            Face f = new Face(new ArrayList<Integer>(), face.color);

            for (int i = 0; i < face.vertexIndices.size(); i++)
            {
                Vector3 v1 = vertices.get(face.vertexIndices.get(i));
                Vector3 v2 = vertices.get(face.vertexIndices.get((i + 1) % face.vertexIndices.size()));

                if (Intersection.pointHalfspace(v1, p)) {
                    if (Intersection.pointHalfspace(v2, p)) {
                        mesh.vertices.add(v2);
                        f.vertexIndices.add(mesh.vertices.size() - 1);
                    } else {
                        mesh.vertices.add(Intersection.planeLine(p, new LineSegment(v1, v2)));
                        f.vertexIndices.add(mesh.vertices.size() - 1);
                    }
                } else {
                    if (Intersection.pointHalfspace(v2, p)) {
                        mesh.vertices.add(Intersection.planeLine(p, new LineSegment(v1, v2)));
                        f.vertexIndices.add(mesh.vertices.size() - 1);

                        mesh.vertices.add(v2);
                        f.vertexIndices.add(mesh.vertices.size() - 1);
                    }
                }
            }

            if (f.vertexIndices.size() >= 3) {
                mesh.faces.add(f);
            }
        }

        return mesh;
    }

    public Mesh cut(Plane p, double gap) {
        Plane p1 = new Plane(p.p.add(p.n.mul(gap / 2)), p.n);
        Plane p2 = new Plane(p.p.sub(p.n.mul(gap / 2)), p.n.neg());
        return clip(p1).union(clip(p2));
    }

    public Mesh shortenFaces(double gap) {
        Mesh mesh = new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());

        for (Face face : faces) {
            Vector3 centroid = new Vector3(0d, 0d, 0d);
            for (int i : face.vertexIndices) {
                centroid = centroid.add(vertices.get(i));
            }
            centroid = centroid.mul(1d / face.vertexIndices.size());

            Face f = new Face(new ArrayList<Integer>(), face.color);
            for (int i : face.vertexIndices) {
                mesh.vertices.add(vertices.get(i).add(centroid.sub(vertices.get(i)).normalized().mul(gap)));
                f.vertexIndices.add(mesh.vertices.size() - 1);
            }

            mesh.faces.add(f);
        }

        return mesh;
    }

    public Mesh softenFaces(double distanceFromCorners)
    {
        Mesh mesh = new Mesh(new ArrayList<Vector3>(), new ArrayList<Face>());

        for (Face face : faces) {
            Face f = new Face(new ArrayList<Integer>(), face.color);

            for (int i = 0; i < face.vertexIndices.size(); i++)
            {
                Vector3 v1 = vertices.get(face.vertexIndices.get(i));
                Vector3 v2 = vertices.get(face.vertexIndices.get((i + 1) % face.vertexIndices.size()));

                if (v2.sub(v1).norm() > 2 * distanceFromCorners)
                {
                    mesh.vertices.add(v1.add(v2.sub(v1).normalized().mul(distanceFromCorners)));
                    f.vertexIndices.add(mesh.vertices.size() - 1);

                    mesh.vertices.add(v2.add(v1.sub(v2).normalized().mul(distanceFromCorners)));
                    f.vertexIndices.add(mesh.vertices.size() - 1);
                }
                else
                {
                    mesh.vertices.add(v1.add(v2).mul(0.5d));
                    f.vertexIndices.add(mesh.vertices.size() - 1);
                }
            }

            mesh.faces.add(f);
        }

        return mesh;
    }

    private static ArrayList<Integer> toArrayList(int[] xs) {
        ArrayList<Integer> ys = new ArrayList<Integer>();
        for (int x : xs) {
            ys.add(x);
        }

        return ys;
    }

    public static Mesh cube(HSLColor[] colors) {
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        vertices.add(new Vector3(-0.5, -0.5, -0.5));
        vertices.add(new Vector3(-0.5, -0.5,  0.5));
        vertices.add(new Vector3(-0.5,  0.5, -0.5));
        vertices.add(new Vector3(-0.5,  0.5,  0.5));
        vertices.add(new Vector3( 0.5, -0.5, -0.5));
        vertices.add(new Vector3( 0.5, -0.5,  0.5));
        vertices.add(new Vector3( 0.5,  0.5, -0.5));
        vertices.add(new Vector3( 0.5,  0.5,  0.5));

        ArrayList<Face> faces = new ArrayList<Face>();
        faces.add(new Face(toArrayList(new int[] { 0, 1, 3, 2 }), colors[0])); // L
        faces.add(new Face(toArrayList(new int[] { 1, 5, 7, 3 }), colors[1])); // B
        faces.add(new Face(toArrayList(new int[] { 0, 4, 5, 1 }), colors[2])); // D
        faces.add(new Face(toArrayList(new int[] { 4, 6, 7, 5 }), colors[3])); // R
        faces.add(new Face(toArrayList(new int[] { 0, 2, 6, 4 }), colors[4])); // F
        faces.add(new Face(toArrayList(new int[] { 2, 3, 7, 6 }), colors[5])); // U

        return new Mesh(vertices, faces);
    }

    public static Mesh tetrahedron(HSLColor[] colors) {
        double a = 1.5;
        double h = Math.sqrt(3d) / 2d * a;
        double h1 = 2d * Math.sqrt(2d) / 3d * h;

        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        vertices.add(new Vector3(     0d,     -h1 / 4d, 2d * h / 3d));
        vertices.add(new Vector3(-a / 2d,     -h1 / 4d,     -h / 3d));
        vertices.add(new Vector3( a / 2d,     -h1 / 4d,     -h / 3d));
        vertices.add(new Vector3(     0d, 3d * h1 / 4d,          0d));

        ArrayList<Face> faces = new ArrayList<Face>();
        faces.add(new Face(toArrayList(new int[] { 0, 1, 2 }), colors[0]));
        faces.add(new Face(toArrayList(new int[] { 0, 3, 1 }), colors[1]));
        faces.add(new Face(toArrayList(new int[] { 0, 2, 3 }), colors[2]));
        faces.add(new Face(toArrayList(new int[] { 1, 3, 2 }), colors[3]));

        return new Mesh(vertices, faces);
    }

    public static Mesh dodecahedron(HSLColor[] colors) {
        double a = 0.85d * 1d / Math.sqrt(3d);
        double b = 0.85d * Math.sqrt((3d - Math.sqrt(5d)) / 6d);
        double c = 0.85d * Math.sqrt((3d + Math.sqrt(5d)) / 6d);

        ArrayList<Vector3> vertices = new ArrayList<Vector3>();

        // 1
        vertices.add(new Vector3( a,  a,  a)); // 0
        vertices.add(new Vector3( b,  c,  0)); // 8
        vertices.add(new Vector3(-b,  c,  0)); // 9
        vertices.add(new Vector3(-a,  a,  a)); // 4
        vertices.add(new Vector3( 0,  b,  c)); // 16

        // 2
        vertices.add(new Vector3( a,  a,  a)); // 0
        vertices.add(new Vector3( 0,  b,  c)); // 16
        vertices.add(new Vector3( 0, -b,  c)); // 17
        vertices.add(new Vector3( a, -a,  a)); // 2
        vertices.add(new Vector3( c,  0,  b)); // 12

        // 3
        vertices.add(new Vector3( c,  0,  b)); // 12
        vertices.add(new Vector3( a, -a,  a)); // 2
        vertices.add(new Vector3( b, -c,  0)); // 10
        vertices.add(new Vector3( a, -a, -a)); // 3
        vertices.add(new Vector3( c,  0, -b)); // 13

        // 4
        vertices.add(new Vector3(-b,  c,  0)); // 9
        vertices.add(new Vector3(-a,  a, -a)); // 5
        vertices.add(new Vector3(-c,  0, -b)); // 15
        vertices.add(new Vector3(-c,  0,  b)); // 14
        vertices.add(new Vector3(-a,  a,  a)); // 4

        // 5
        vertices.add(new Vector3( a, -a, -a)); // 3
        vertices.add(new Vector3( 0, -b, -c)); // 19
        vertices.add(new Vector3( 0,  b, -c)); // 18
        vertices.add(new Vector3( a,  a, -a)); // 1
        vertices.add(new Vector3( c,  0, -b)); // 13

        // 6
        vertices.add(new Vector3(-a, -a, -a)); // 7
        vertices.add(new Vector3(-b, -c,  0)); // 11
        vertices.add(new Vector3(-a, -a,  a)); // 6
        vertices.add(new Vector3(-c,  0,  b)); // 14
        vertices.add(new Vector3(-c,  0, -b)); // 15

        // 7
        vertices.add(new Vector3( a,  a,  a)); // 0
        vertices.add(new Vector3( c,  0,  b)); // 12
        vertices.add(new Vector3( c,  0, -b)); // 13
        vertices.add(new Vector3( a,  a, -a)); // 1
        vertices.add(new Vector3( b,  c,  0)); // 8

        // 8
        vertices.add(new Vector3( b,  c,  0)); // 8
        vertices.add(new Vector3( a,  a, -a)); // 1
        vertices.add(new Vector3( 0,  b, -c)); // 18
        vertices.add(new Vector3(-a,  a, -a)); // 5
        vertices.add(new Vector3(-b,  c,  0)); // 9

        // 9
        vertices.add(new Vector3( 0,  b,  c)); // 16
        vertices.add(new Vector3(-a,  a,  a)); // 4
        vertices.add(new Vector3(-c,  0,  b)); // 14
        vertices.add(new Vector3(-a, -a,  a)); // 6
        vertices.add(new Vector3( 0, -b,  c)); // 17

        // 10
        vertices.add(new Vector3(-a, -a,  a)); // 6
        vertices.add(new Vector3(-b, -c,  0)); // 11
        vertices.add(new Vector3( b, -c,  0)); // 10
        vertices.add(new Vector3( a, -a,  a)); // 2
        vertices.add(new Vector3( 0, -b,  c)); // 17

        // 11
        vertices.add(new Vector3(-a, -a, -a)); // 7
        vertices.add(new Vector3(-c,  0, -b)); // 15
        vertices.add(new Vector3(-a,  a, -a)); // 5
        vertices.add(new Vector3( 0,  b, -c)); // 18
        vertices.add(new Vector3( 0, -b, -c)); // 19

        // 12
        vertices.add(new Vector3(-a, -a, -a)); // 7
        vertices.add(new Vector3( 0, -b, -c)); // 19
        vertices.add(new Vector3( a, -a, -a)); // 3
        vertices.add(new Vector3( b, -c,  0)); // 10
        vertices.add(new Vector3(-b, -c,  0)); // 11

        ArrayList<Face> faces = new ArrayList<Face>();
        faces.add(new Face(toArrayList(new int[] {  0,  1,  2,  3,  4 }), colors[ 0]));
        faces.add(new Face(toArrayList(new int[] {  5,  6,  7,  8,  9 }), colors[ 1]));
        faces.add(new Face(toArrayList(new int[] { 10, 11, 12, 13, 14 }), colors[ 2]));
        faces.add(new Face(toArrayList(new int[] { 15, 16, 17, 18, 19 }), colors[ 3]));
        faces.add(new Face(toArrayList(new int[] { 20, 21, 22, 23, 24 }), colors[ 4]));
        faces.add(new Face(toArrayList(new int[] { 25, 26, 27, 28, 29 }), colors[ 5]));
        faces.add(new Face(toArrayList(new int[] { 30, 31, 32, 33, 34 }), colors[ 6]));
        faces.add(new Face(toArrayList(new int[] { 35, 36, 37, 38, 39 }), colors[ 7]));
        faces.add(new Face(toArrayList(new int[] { 40, 41, 42, 43, 44 }), colors[ 8]));
        faces.add(new Face(toArrayList(new int[] { 45, 46, 47, 48, 49 }), colors[ 9]));
        faces.add(new Face(toArrayList(new int[] { 50, 51, 52, 53, 54 }), colors[10]));
        faces.add(new Face(toArrayList(new int[] { 55, 56, 57, 58, 59 }), colors[11]));

        return new Mesh(vertices, faces);
    }
}

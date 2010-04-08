package com.puzzletimer.graphics;

import java.util.ArrayList;

import com.puzzletimer.geometry.Intersection;
import com.puzzletimer.geometry.LineSegment;
import com.puzzletimer.geometry.Plane;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;

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
	
	public static Mesh cube() {
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
		faces.add(new Face(toArrayList(new int[] { 0, 1, 3, 2 }), new HSLColor( 25, 100,  50))); // L - orange
		faces.add(new Face(toArrayList(new int[] { 1, 5, 7, 3 }), new HSLColor(235, 100,  30))); // B - blue
		faces.add(new Face(toArrayList(new int[] { 0, 4, 5, 1 }), new HSLColor( 55, 100,  50))); // D - yellow
		faces.add(new Face(toArrayList(new int[] { 4, 6, 7, 5 }), new HSLColor(  0,  85,  45))); // R - red
		faces.add(new Face(toArrayList(new int[] { 0, 2, 6, 4 }), new HSLColor(120,  65,  40))); // F - green
		faces.add(new Face(toArrayList(new int[] { 2, 3, 7, 6 }), new HSLColor(  0,   0, 100))); // U - white

		return new Mesh(vertices, faces);
	}
	
	public static Mesh tetrahedron() {
        double a = 1.5;
        double h = Math.sqrt(3d) / 2d * a;
        double h1 = 2d * Math.sqrt(2d) / 3d * h;
        
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        vertices.add(new Vector3(     0d,     -h1 / 4d, 2d * h / 3d));
        vertices.add(new Vector3(-a / 2d,     -h1 / 4d,     -h / 3d));
        vertices.add(new Vector3( a / 2d,     -h1 / 4d,     -h / 3d));
        vertices.add(new Vector3(     0d, 3d * h1 / 4d,          0d));

        ArrayList<Face> faces = new ArrayList<Face>();
        faces.add(new Face(toArrayList(new int[] { 0, 1, 2 }), new HSLColor(235, 100,  30))); // blue
        faces.add(new Face(toArrayList(new int[] { 0, 3, 1 }), new HSLColor(120,  65,  40))); // green
        faces.add(new Face(toArrayList(new int[] { 0, 2, 3 }), new HSLColor( 55, 100,  50))); // yellow
        faces.add(new Face(toArrayList(new int[] { 1, 3, 2 }), new HSLColor(  0,  85,  45))); // red

        return new Mesh(vertices, faces);
	}	
}

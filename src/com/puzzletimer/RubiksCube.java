package com.puzzletimer;

import java.util.HashMap;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.Scramble;

class Twist {
	public Plane plane;
	public double angle;
	
	public Twist(Plane plane, double angle) {
		this.plane = plane;
		this.angle = angle;
	}
}

public class RubiksCube {
	public static Mesh getMesh(Scramble s)
	{
		Plane planeL = new Plane(new Vector3(-0.166, 0, 0), new Vector3(-1, 0, 0));
		Plane planeR = new Plane(new Vector3( 0.166, 0, 0), new Vector3( 1, 0, 0));
		Plane planeD = new Plane(new Vector3(0, -0.166, 0), new Vector3(0, -1, 0));
		Plane planeU = new Plane(new Vector3(0,  0.166, 0), new Vector3(0,  1, 0));
		Plane planeF = new Plane(new Vector3(0, 0, -0.166), new Vector3(0, 0, -1));
		Plane planeB = new Plane(new Vector3(0, 0,  0.166), new Vector3(0, 0,  1));
		
		Mesh cube = Mesh.cube()
			.cut(planeL, 0)
			.cut(planeR, 0)
			.cut(planeD, 0)
			.cut(planeU, 0)
			.cut(planeF, 0)
			.cut(planeB, 0)
			.shortenFaces(0.03)
			.softenFaces(0.015)
			.softenFaces(0.005);
		
		HashMap<Integer, Twist> twists = new HashMap<Integer, Twist>();
		twists.put(Move.L,  new Twist(planeL,  Math.PI / 2));
		twists.put(Move.L2, new Twist(planeL,  Math.PI));
		twists.put(Move.L3, new Twist(planeL, -Math.PI / 2));		
		twists.put(Move.R,  new Twist(planeR,  Math.PI / 2));
		twists.put(Move.R2, new Twist(planeR,  Math.PI));
		twists.put(Move.R3, new Twist(planeR, -Math.PI / 2));		
		twists.put(Move.D,  new Twist(planeD,  Math.PI / 2));
		twists.put(Move.D2, new Twist(planeD,  Math.PI));
		twists.put(Move.D3, new Twist(planeD, -Math.PI / 2));		
		twists.put(Move.U,  new Twist(planeU,  Math.PI / 2));
		twists.put(Move.U2, new Twist(planeU,  Math.PI));
		twists.put(Move.U3, new Twist(planeU, -Math.PI / 2));		
		twists.put(Move.F,  new Twist(planeF,  Math.PI / 2));
		twists.put(Move.F2, new Twist(planeF,  Math.PI));
		twists.put(Move.F3, new Twist(planeF, -Math.PI / 2));		
		twists.put(Move.B,  new Twist(planeB,  Math.PI / 2));
		twists.put(Move.B2, new Twist(planeB,  Math.PI));
		twists.put(Move.B3, new Twist(planeB, -Math.PI / 2));		
		
		for (Integer move : s.moves) {
			Twist t = twists.get(move);
			cube = cube.transformHalfspace(
				Matrix33.rotation(t.plane.n, t.angle),
				t.plane);
		}
		
		return cube 
			.transform(Matrix33.rotationY(-Math.PI / 6))
			.transform(Matrix33.rotationX(Math.PI / 7));
	}
}

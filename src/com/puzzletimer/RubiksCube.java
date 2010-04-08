package com.puzzletimer;

import java.util.HashMap;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.RubiksCubeMove;
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
		
		HashMap<Move, Twist> twists = new HashMap<Move, Twist>();
		twists.put(RubiksCubeMove.L,  new Twist(planeL,  Math.PI / 2));
		twists.put(RubiksCubeMove.L2, new Twist(planeL,  Math.PI));
		twists.put(RubiksCubeMove.L3, new Twist(planeL, -Math.PI / 2));		
		twists.put(RubiksCubeMove.R,  new Twist(planeR,  Math.PI / 2));
		twists.put(RubiksCubeMove.R2, new Twist(planeR,  Math.PI));
		twists.put(RubiksCubeMove.R3, new Twist(planeR, -Math.PI / 2));		
		twists.put(RubiksCubeMove.D,  new Twist(planeD,  Math.PI / 2));
		twists.put(RubiksCubeMove.D2, new Twist(planeD,  Math.PI));
		twists.put(RubiksCubeMove.D3, new Twist(planeD, -Math.PI / 2));		
		twists.put(RubiksCubeMove.U,  new Twist(planeU,  Math.PI / 2));
		twists.put(RubiksCubeMove.U2, new Twist(planeU,  Math.PI));
		twists.put(RubiksCubeMove.U3, new Twist(planeU, -Math.PI / 2));		
		twists.put(RubiksCubeMove.F,  new Twist(planeF,  Math.PI / 2));
		twists.put(RubiksCubeMove.F2, new Twist(planeF,  Math.PI));
		twists.put(RubiksCubeMove.F3, new Twist(planeF, -Math.PI / 2));		
		twists.put(RubiksCubeMove.B,  new Twist(planeB,  Math.PI / 2));
		twists.put(RubiksCubeMove.B2, new Twist(planeB,  Math.PI));
		twists.put(RubiksCubeMove.B3, new Twist(planeB, -Math.PI / 2));		
		
		for (Move move : s.moves) {
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

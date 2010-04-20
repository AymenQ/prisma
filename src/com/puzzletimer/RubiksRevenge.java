package com.puzzletimer;

import java.util.HashMap;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.HSLColor;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.RubiksRevengeMove;
import com.puzzletimer.scrambles.RubiksRevengeRandomScrambler;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;

public class RubiksRevenge implements Puzzle {
	private Scrambler scrambler;
	
	public RubiksRevenge() {
		scrambler = new RubiksRevengeRandomScrambler(40); 
	}
	
	@Override
	public Scrambler getScrambler()
	{
		return scrambler; 
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
	public Mesh getMesh(Scramble s)
	{
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
		
		HashMap<Move, Twist> twists = new HashMap<Move, Twist>();
        twists.put(RubiksRevengeMove.L,   new Twist(planeL,   Math.PI / 2));
        twists.put(RubiksRevengeMove.Lw,  new Twist(planeLw,  Math.PI / 2));
        twists.put(RubiksRevengeMove.L2,  new Twist(planeL,   Math.PI));
        twists.put(RubiksRevengeMove.Lw2, new Twist(planeLw,  Math.PI));
        twists.put(RubiksRevengeMove.L3,  new Twist(planeL,  -Math.PI / 2));
        twists.put(RubiksRevengeMove.Lw3, new Twist(planeLw, -Math.PI / 2));
        twists.put(RubiksRevengeMove.R,   new Twist(planeR,   Math.PI / 2));
        twists.put(RubiksRevengeMove.Rw,  new Twist(planeRw,  Math.PI / 2));
        twists.put(RubiksRevengeMove.R2,  new Twist(planeR,   Math.PI));
        twists.put(RubiksRevengeMove.Rw2, new Twist(planeRw,  Math.PI));
        twists.put(RubiksRevengeMove.R3,  new Twist(planeR,  -Math.PI / 2));
        twists.put(RubiksRevengeMove.Rw3, new Twist(planeRw, -Math.PI / 2));
        twists.put(RubiksRevengeMove.D,   new Twist(planeD,   Math.PI / 2));
        twists.put(RubiksRevengeMove.Dw,  new Twist(planeDw,  Math.PI / 2));
        twists.put(RubiksRevengeMove.D2,  new Twist(planeD,   Math.PI));
        twists.put(RubiksRevengeMove.Dw2, new Twist(planeDw,  Math.PI));
        twists.put(RubiksRevengeMove.D3,  new Twist(planeD,  -Math.PI / 2));
        twists.put(RubiksRevengeMove.Dw3, new Twist(planeDw, -Math.PI / 2));
        twists.put(RubiksRevengeMove.U,   new Twist(planeU,   Math.PI / 2));
        twists.put(RubiksRevengeMove.Uw,  new Twist(planeUw,  Math.PI / 2));
        twists.put(RubiksRevengeMove.U2,  new Twist(planeU,   Math.PI));
        twists.put(RubiksRevengeMove.Uw2, new Twist(planeUw,  Math.PI));
        twists.put(RubiksRevengeMove.U3,  new Twist(planeU,  -Math.PI / 2));
        twists.put(RubiksRevengeMove.Uw3, new Twist(planeUw, -Math.PI / 2));
        twists.put(RubiksRevengeMove.F,   new Twist(planeF,   Math.PI / 2));
        twists.put(RubiksRevengeMove.Fw,  new Twist(planeFw,  Math.PI / 2));
        twists.put(RubiksRevengeMove.F2,  new Twist(planeF,   Math.PI));
        twists.put(RubiksRevengeMove.Fw2, new Twist(planeFw,  Math.PI));
        twists.put(RubiksRevengeMove.F3,  new Twist(planeF,  -Math.PI / 2));
        twists.put(RubiksRevengeMove.Fw3, new Twist(planeFw, -Math.PI / 2));
        twists.put(RubiksRevengeMove.B,   new Twist(planeB,   Math.PI / 2));
        twists.put(RubiksRevengeMove.Bw,  new Twist(planeBw,  Math.PI / 2));
        twists.put(RubiksRevengeMove.B2,  new Twist(planeB,   Math.PI));
        twists.put(RubiksRevengeMove.Bw2, new Twist(planeBw,  Math.PI));
        twists.put(RubiksRevengeMove.B3,  new Twist(planeB,  -Math.PI / 2));
        twists.put(RubiksRevengeMove.Bw3, new Twist(planeBw, -Math.PI / 2));
		
		for (Move move : s.moves) {
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

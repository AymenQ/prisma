package com.puzzletimer;

import java.util.HashMap;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.scrambles.MegaminxMove;
import com.puzzletimer.scrambles.MegaminxRandomScrambler;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;

public class Megaminx implements Puzzle {
	private Scrambler scrambler;
	
	public Megaminx()
	{
		scrambler = new MegaminxRandomScrambler(); 
	}
	
	@Override
	public Scrambler getScrambler()
	{
		return scrambler; 
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
	public Mesh getMesh(Scramble s)
	{
		Mesh mesh = Mesh.dodecahedron()
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

		HashMap<Move, Twist> twists = new HashMap<Move, Twist>();
		twists.put(MegaminxMove.R2, new Twist(planeR,  4 * Math.PI / 5));
		twists.put(MegaminxMove.R7, new Twist(planeR, -4 * Math.PI / 5));
		twists.put(MegaminxMove.D2, new Twist(planeD,  4 * Math.PI / 5));
		twists.put(MegaminxMove.D7, new Twist(planeD, -4 * Math.PI / 5));
		twists.put(MegaminxMove.U,  new Twist(planeU,  2 * Math.PI / 5));
		twists.put(MegaminxMove.U6, new Twist(planeU, -2 * Math.PI / 5));

		for (Move move : s.moves) {
			Twist t = twists.get(move);
			mesh = mesh.transformHalfspace(
				Matrix33.rotation(t.plane.n, t.angle),
				t.plane);
		}

		return mesh
			.transform(Matrix33.rotationY(Math.PI / 16));
	}
}

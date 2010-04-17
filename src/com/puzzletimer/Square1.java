package com.puzzletimer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.puzzletimer.geometry.Plane;
import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.linearalgebra.Matrix33;
import com.puzzletimer.linearalgebra.Vector3;
import com.puzzletimer.scrambles.Move;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;
import com.puzzletimer.scrambles.Square1RandomScrambler;

public class Square1 implements Puzzle {
	private Scrambler scrambler;
	
	public Square1() {
		scrambler = new Square1RandomScrambler(20); 
	}
	
	@Override
	public Scrambler getScrambler()
	{
		return scrambler; 
	}
		
	@Override
	public Mesh getMesh(Scramble s)
	{
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


		Mesh cube = Mesh.cube()
			.cut(planeD, 0.01)
			.cut(planeU, 0.01)
			.cut(planeR, 0.01)
			.cut(p1, 0.01)
			.cut(p2, 0.01)
			.cut(p3, 0.01)
			.shortenFaces(0.02)
			.softenFaces(0.015)
			.softenFaces(0.005);

		Mesh topLayer = cube.clip(planeU);
		Mesh bottomLayer = cube.clip(planeD);

		
		Mesh bandagedCube = Mesh.cube()
			.cut(planeD, 0.01)
			.cut(planeU, 0.01)
			.cut(planeR, 0.01)
			.shortenFaces(0.02)
			.softenFaces(0.015)
			.softenFaces(0.005);

		Mesh middleLayer = bandagedCube
	    	.clip(new Plane(planeU.p, planeU.n.neg()))
	    	.clip(new Plane(planeD.p, planeD.n.neg()));
		

		cube = topLayer.union(middleLayer).union(bottomLayer);


		// i can't believe i am doing this...
		Pattern p = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");
		for (Move m : s.moves) {
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

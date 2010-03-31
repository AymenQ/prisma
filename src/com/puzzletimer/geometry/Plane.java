package com.puzzletimer.geometry;

import com.puzzletimer.linearalgebra.Vector3;

public class Plane {
	public Vector3 p;
	public Vector3 n;
	
	public Plane(Vector3 p, Vector3 n) {
		this.p = p;
		this.n = n;
	}
}

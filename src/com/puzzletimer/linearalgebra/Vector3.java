package com.puzzletimer.linearalgebra;

public class Vector3 {
	public double x;
	public double y;
	public double z;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double norm() {
		return Math.sqrt(this.dot(this));
	}
	
	public Vector3 normalized() {
		return this.mul(1d / norm());
	}
	
	public Vector3 neg() {
		return new Vector3(-x, -y, -z);
	}

	public Vector3 add(Vector3 v) {
	    return new Vector3(x + v.x, y + v.y, z + v.z); 
	}
	    
	public Vector3 sub(Vector3 v) {
	    return new Vector3(x - v.x, y - v.y, z - v.z); 
	}

	public Vector3 mul(double s) {
		return new Vector3(s * x, s * y, s * z);
	}
	
	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	public Vector3 cross(Vector3 v) {
		return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
	}
}

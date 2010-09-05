package com.puzzletimer.graphics.geometry;

import com.puzzletimer.graphics.algebra.Vector3;

public class Plane {
    public Vector3 p;
    public Vector3 n;

    public Plane(Vector3 p, Vector3 n) {
        this.p = p;
        this.n = n;
    }

    public static Plane fromVectors(Vector3 v1, Vector3 v2, Vector3 v3) {
        Vector3 p = v1.add(v2).add(v3).mul(1d / 3d);
        Vector3 n = v2.sub(v1).cross(v3.sub(v1)).normalized();
        return new Plane(p, n);
    }
}

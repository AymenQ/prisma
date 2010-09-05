package com.puzzletimer.graphics.geometry;

import com.puzzletimer.graphics.algebra.Vector3;

public class LineSegment {
    public Vector3 p;
    public Vector3 q;

    public LineSegment(Vector3 p, Vector3 q) {
        this.p = p;
        this.q = q;
    }
}

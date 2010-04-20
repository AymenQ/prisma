package com.puzzletimer.geometry;

import com.puzzletimer.linearalgebra.Vector3;

public class Intersection {
    public static boolean pointHalfspace(Vector3 point, Plane halfspace) {
        return point.sub(halfspace.p).dot(halfspace.n) >= 0d;
    }
    
    public static Vector3 planeLine(Plane plane, LineSegment lineSegment) {
        Vector3 intersection = null;
        
        Vector3 d = lineSegment.q.sub(lineSegment.p);
        double dn = d.dot(plane.n);
        
        if (Math.abs(dn) > 0.001)
        {
            double t = -(plane.n.dot(lineSegment.p) + plane.n.neg().dot(plane.p)) / dn;
            if (0d <= t && t <= 1d)
            {
                intersection = lineSegment.p.add(d.mul(t));
            }
        }

        return intersection;
    }
}

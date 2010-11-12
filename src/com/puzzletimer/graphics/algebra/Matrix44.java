package com.puzzletimer.graphics.algebra;

public class Matrix44 {
    public double[][] values;

    public Matrix44(double[][] matrix) {
        this.values = matrix;
    }

    public Matrix44 mul(Matrix44 m) {
        double[][] vals = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                vals[i][j] = 0d;
                for (int k = 0; k < 4; k++) {
                    vals[i][j] += this.values[i][k] * m.values[k][j];
                }
            }
        }

        return new Matrix44(vals);
    }

    public Vector3 mul(Vector3 v) {
        double x = this.values[0][0] * v.x + this.values[0][1] * v.y + this.values[0][2] * v.z + this.values[0][3];
        double y = this.values[1][0] * v.x + this.values[1][1] * v.y + this.values[1][2] * v.z + this.values[1][3];
        double z = this.values[2][0] * v.x + this.values[2][1] * v.y + this.values[2][2] * v.z + this.values[2][3];
        return new Vector3(x, y, z);
    }

    public static Matrix44 translation(Vector3 v) {
        return new Matrix44(new double[][] {
            new double[] { 1d, 0d, 0d, v.x },
            new double[] { 0d, 1d, 0d, v.y },
            new double[] { 0d, 0d, 1d, v.z },
            new double[] { 0d, 0d, 0d,  1d },
        });
    }

    public static Matrix44 rotationX(double a) {
        return new Matrix44(new double[][] {
            new double[] { 1d,           0d,          0d, 0d },
            new double[] { 0d,  Math.cos(a), Math.sin(a), 0d },
            new double[] { 0d, -Math.sin(a), Math.cos(a), 0d },
            new double[] { 0d,           0d,          0d, 1d },
        });
    }

    public static Matrix44 rotationY(double a) {
        return new Matrix44(new double[][] {
            new double[] { Math.cos(a), 0d, -Math.sin(a), 0d },
            new double[] { 0d,          1d,           0d, 0d },
            new double[] { Math.sin(a), 0d,  Math.cos(a), 0d },
            new double[] { 0d,          0d,           0d, 1d },
        });
    }

    public static Matrix44 rotationZ(double a) {
        return new Matrix44(new double[][] {
            new double[] {  Math.cos(a), Math.sin(a), 0d, 0d },
            new double[] { -Math.sin(a), Math.cos(a), 0d, 0d },
            new double[] {           0d,          0d, 1d, 0d },
            new double[] {           0d,          0d, 0d, 1d },
        });
    }

    public static Matrix44 rotation(Vector3 v, double a) {
        double c = Math.cos(a);
        double s = Math.sin(a);

        double x = v.x;
        double y = v.y;
        double z = v.z;

        return new Matrix44(new double[][] {
            new double[] { 1d + (1d - c) * (x * x - 1d),    -z * s + (1d - c) * x * y,     y * s + (1d - c) * x * z, 0d },
            new double[] {     z * s + (1d - c) * x * y, 1d + (1d - c) * (y * y - 1d),    -x * s + (1d - c) * y * z, 0d },
            new double[] {    -y * s + (1d - c) * x * z,     x * s + (1d - c) * y * z, 1d + (1d - c) * (z * z - 1d), 0d },
            new double[] {                           0d,                           0d,                           0d, 1d },
        });
    }
}

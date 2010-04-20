package com.puzzletimer.linearalgebra;

public class Matrix33 {
    public double[][] values;

    public Matrix33(double[][] matrix) {
        this.values = matrix;
    }

    public Matrix33 mul(Matrix33 m) {
        double[][] vals = new double[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                vals[i][j] = 0d;
                for (int k = 0; k < 3; k++) {
                    vals[i][j] += values[i][k] * m.values[k][j];
                }
            }
        }

        return new Matrix33(vals);
    }

    public Vector3 mul(Vector3 v) {
        double x = values[0][0] * v.x + values[0][1] * v.y + values[0][2] * v.z;
        double y = values[1][0] * v.x + values[1][1] * v.y + values[1][2] * v.z;
        double z = values[2][0] * v.x + values[2][1] * v.y + values[2][2] * v.z;
        return new Vector3(x, y, z);
    }

    public static Matrix33 rotationX(double a) {
        return new Matrix33(new double[][] {
            new double[] { 1d,           0d,          0d },
            new double[] { 0d,  Math.cos(a), Math.sin(a) },
            new double[] { 0d, -Math.sin(a), Math.cos(a) },
        });
    }

    public static Matrix33 rotationY(double a) {
        return new Matrix33(new double[][] {
            new double[] { Math.cos(a), 0d, -Math.sin(a) },
            new double[] { 0d,          1d,           0d },
            new double[] { Math.sin(a), 0d,  Math.cos(a) },
        });
    }

    public static Matrix33 rotationZ(double a) {
        return new Matrix33(new double[][] {
            new double[] {  Math.cos(a), Math.sin(a), 0d },
            new double[] { -Math.sin(a), Math.cos(a), 0d },
            new double[] {           0d,          0d, 1d },
        });
    }

    public static Matrix33 rotation(Vector3 v, double a) {
        double c = Math.cos(a);
        double s = Math.sin(a);

        double x = v.x;
        double y = v.y;
        double z = v.z;

        return new Matrix33(new double[][] {
            new double[] { 1d + (1d - c) * (x * x - 1d),    -z * s + (1d - c) * x * y,     y * s + (1d - c) * x * z },
            new double[] {     z * s + (1d - c) * x * y, 1d + (1d - c) * (y * y - 1d),    -x * s + (1d - c) * y * z },
            new double[] {    -y * s + (1d - c) * x * z,     x * s + (1d - c) * y * z, 1d + (1d - c) * (z * z - 1d) },
        });
    }
}

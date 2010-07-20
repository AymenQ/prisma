package com.puzzletimer.solvers;

public class IndexMapping {
    public static int permutationToIndex(byte[] permutation) {
        int index = 0;
        for (int i = 0; i < permutation.length - 1; i++) {
            index *= permutation.length - i;
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[i] > permutation[j]) {
                    index++;
                }
            }
        }

        return index;
    }

    public static byte[] indexToPermutation(int index, int length) {
        byte[] permutation = new byte[length];
        permutation[length - 1] = 0;
        for (int i = length - 2; i >= 0; i--) {
            permutation[i] = (byte) (index % (length - i));
            index /= length - i;
            for (int j = i + 1; j < length; j++) {
                if (permutation[j] >= permutation[i]) {
                    permutation[j]++;
                }
            }
        }

        return permutation;
    }

    public static int orientationToIndex(byte[] orientation, int nValues) {
        int index = 0;
        for (int i = 0; i < orientation.length - 1; i++) {
            index = nValues * index + orientation[i];
        }

        return index;
    }

    public static byte[] indexToOrientation(int index, int nValues, int length) {
        byte[] orientation = new byte[length];
        orientation[length - 1] = 0;
        for (int i = length - 2; i >= 0; i--) {
            orientation[i] = (byte) (index % nValues);
            index /= nValues;

            orientation[length - 1] += orientation[i];
        }
        orientation[length - 1] = (byte) ((nValues - orientation[length - 1] % nValues) % nValues);

        return orientation;
    }
}

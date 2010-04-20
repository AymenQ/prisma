package com.puzzletimer.graphics;

import java.util.ArrayList;

public class Face {
    public ArrayList<Integer> vertexIndices;
    public HSLColor color;

    public Face(ArrayList<Integer> vertexIndices, HSLColor color) {
        this.vertexIndices = vertexIndices;
        this.color = color;
    }
}

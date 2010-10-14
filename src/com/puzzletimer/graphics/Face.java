package com.puzzletimer.graphics;

import java.awt.Color;
import java.util.ArrayList;

public class Face {
    public ArrayList<Integer> vertexIndices;
    public Color color;

    public Face(ArrayList<Integer> vertexIndices, Color color) {
        this.vertexIndices = vertexIndices;
        this.color = color;
    }
}

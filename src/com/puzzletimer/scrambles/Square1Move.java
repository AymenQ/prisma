package com.puzzletimer.scrambles;

public class Square1Move implements Move {
    int top;
    int bottom;

    public Square1Move(int top, int bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public String toString()
    {
        return "(" + top + "," + bottom + ")";
    }
}

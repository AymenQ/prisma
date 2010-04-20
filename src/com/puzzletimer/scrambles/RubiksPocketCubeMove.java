package com.puzzletimer.scrambles;

public enum RubiksPocketCubeMove implements Move {
    x("x"), x2("x2"), x3("x'"),
    y("y"), y2("y2"), y3("y'"),
    z("z"), z2("z2"), z3("z'"),
    F("F"), F2("F2"), F3("F'"),
    R("R"), R2("R2"), R3("R'"),
    B("B"), B2("B2"), B3("B'"),
    L("L"), L2("L2"), L3("L'"),
    U("U"), U2("U2"), U3("U'"),
    D("D"), D2("D2"), D3("D'");
    
    private String description;
    
    private RubiksPocketCubeMove(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}

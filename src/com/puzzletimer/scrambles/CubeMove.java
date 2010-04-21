package com.puzzletimer.scrambles;

public enum CubeMove implements Move {
    F("F"), Fw("Fw"), F2("F2"), Fw2("Fw2"), F3("F'"), Fw3("Fw'"),
    R("R"), Rw("Rw"), R2("R2"), Rw2("Rw2"), R3("R'"), Rw3("Rw'"),
    B("B"), Bw("Bw"), B2("B2"), Bw2("Bw2"), B3("B'"), Bw3("Bw'"),
    L("L"), Lw("Lw"), L2("L2"), Lw2("Lw2"), L3("L'"), Lw3("Lw'"),
    U("U"), Uw("Uw"), U2("U2"), Uw2("Uw2"), U3("U'"), Uw3("Uw'"),
    D("D"), Dw("Dw"), D2("D2"), Dw2("Dw2"), D3("D'"), Dw3("Dw'");

    private String description;

    private CubeMove(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

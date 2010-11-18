package com.puzzletimer.tips;

import java.util.HashMap;

public class TipperProvider {
    private Tipper[] tippers;
    private HashMap<String, Tipper> tipperMap;

    public TipperProvider() {
        this.tippers = new Tipper[] {
            new RubiksCubeTipper(),
            new Square1Tipper(),
        };

        this.tipperMap = new HashMap<String, Tipper>();
        for (Tipper tipper : this.tippers) {
            this.tipperMap.put(tipper.getPuzzleId(), tipper);
        }
    }

    public Tipper get(String puzzleId) {
        return this.tipperMap.get(puzzleId);
    }
}

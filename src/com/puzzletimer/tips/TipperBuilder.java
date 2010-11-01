package com.puzzletimer.tips;

import java.util.HashMap;

public class TipperBuilder {
    private static Tipper[] tippers;
    private static HashMap<String, Tipper> tipperMap;

    static {
        tippers = new Tipper[] {
            new RubiksCubeTipper(),
            new Square1Tipper(),
        };

        tipperMap = new HashMap<String, Tipper>();
        for (Tipper tipper : tippers) {
            tipperMap.put(tipper.getPuzzleId(), tipper);
        }
    }

    public static Tipper getTipper(String puzzleId) {
        return tipperMap.get(puzzleId);
    }
}

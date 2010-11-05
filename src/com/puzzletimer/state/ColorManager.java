package com.puzzletimer.state;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class ColorManager {
    private ArrayList<ColorListener> listeners;
    private HashMap<String, HashMap<String, Color>> colors;
    private HashMap<String, HashMap<String, Color>> defaultColors;

    public ColorManager() {
        this.listeners = new ArrayList<ColorListener>();
        this.colors = new HashMap<String, HashMap<String,Color>>();

        // megaminx
        HashMap<String, Color> megaminxColors = new HashMap<String, Color>();
        megaminxColors.put("Face 01", new Color( 13, 166, 242)); // light blue
        megaminxColors.put("Face 02", new Color(255,  85,   0)); // orange
        megaminxColors.put("Face 03", new Color(212,  17,  17)); // red
        megaminxColors.put("Face 04", new Color(153,  77,   0)); // brown
        megaminxColors.put("Face 05", new Color(  0, 153,   0)); // green
        megaminxColors.put("Face 06", new Color(247, 100, 179)); // pink
        megaminxColors.put("Face 07", new Color(147,  13, 242)); // purple
        megaminxColors.put("Face 08", new Color(255, 255, 255)); // white
        megaminxColors.put("Face 09", new Color(  0, 255,  43)); // light green
        megaminxColors.put("Face 10", new Color(255, 234,   0)); // yellow
        megaminxColors.put("Face 11", new Color( 13, 242, 242)); // cyan
        megaminxColors.put("Face 12", new Color(  0,  13, 153)); // blue
        this.colors.put("MEGAMINX", megaminxColors);

        // other
        HashMap<String, Color> otherColors = new HashMap<String, Color>();
        this.colors.put("OTHER", otherColors);

        // 5x5x5 cube
        HashMap<String, Color> professorsCubeColors = new HashMap<String, Color>();
        professorsCubeColors.put("Face L", new Color(255,  85,   0)); // orange
        professorsCubeColors.put("Face B", new Color(  0,  13, 153)); // blue
        professorsCubeColors.put("Face D", new Color(255, 234,   0)); // yellow
        professorsCubeColors.put("Face R", new Color(212,  17,  17)); // red
        professorsCubeColors.put("Face F", new Color(  0, 153,   0)); // green
        professorsCubeColors.put("Face U", new Color(255, 255, 255)); // white
        this.colors.put("5x5x5-CUBE", professorsCubeColors);

        // pyraminx
        HashMap<String, Color> pyraminxColors = new HashMap<String, Color>();
        pyraminxColors.put("Face U", new Color(  0,  13, 153)); // blue
        pyraminxColors.put("Face R", new Color(  0, 153,   0)); // green
        pyraminxColors.put("Face L", new Color(255, 234,   0)); // yellow
        pyraminxColors.put("Face B", new Color(212,  17,  17)); // red
        this.colors.put("PYRAMINX", pyraminxColors);

        // rubik's cube
        HashMap<String, Color> rubiksCubeColors = new HashMap<String, Color>();
        rubiksCubeColors.put("Face L", new Color(255,  85,   0)); // orange
        rubiksCubeColors.put("Face B", new Color(  0,  13, 153)); // blue
        rubiksCubeColors.put("Face D", new Color(255, 234,   0)); // yellow
        rubiksCubeColors.put("Face R", new Color(212,  17,  17)); // red
        rubiksCubeColors.put("Face F", new Color(  0, 153,   0)); // green
        rubiksCubeColors.put("Face U", new Color(255, 255, 255)); // white
        this.colors.put("RUBIKS-CUBE", rubiksCubeColors);

        // rubik's pocket cube
        HashMap<String, Color> rubiksPocketCubeColors = new HashMap<String, Color>();
        rubiksPocketCubeColors.put("Face L", new Color(255,  85,   0)); // orange
        rubiksPocketCubeColors.put("Face B", new Color(  0,  13, 153)); // blue
        rubiksPocketCubeColors.put("Face D", new Color(255, 234,   0)); // yellow
        rubiksPocketCubeColors.put("Face R", new Color(212,  17,  17)); // red
        rubiksPocketCubeColors.put("Face F", new Color(  0, 153,   0)); // green
        rubiksPocketCubeColors.put("Face U", new Color(255, 255, 255)); // white
        this.colors.put("2x2x2-CUBE", rubiksPocketCubeColors);

        // rubik's revenge
        HashMap<String, Color> rubiksRevengeColors = new HashMap<String, Color>();
        rubiksRevengeColors.put("Face L", new Color(255,  85,   0)); // orange
        rubiksRevengeColors.put("Face B", new Color(  0,  13, 153)); // blue
        rubiksRevengeColors.put("Face D", new Color(255, 234,   0)); // yellow
        rubiksRevengeColors.put("Face R", new Color(212,  17,  17)); // red
        rubiksRevengeColors.put("Face F", new Color(  0, 153,   0)); // green
        rubiksRevengeColors.put("Face U", new Color(255, 255, 255)); // white
        this.colors.put("4x4x4-CUBE", rubiksRevengeColors);

        // square-1
        HashMap<String, Color> square1Colors = new HashMap<String, Color>();
        square1Colors.put("Face L", new Color(255, 234,   0)); // yellow
        square1Colors.put("Face B", new Color(212,  17,  17)); // red
        square1Colors.put("Face D", new Color(  0, 153,   0)); // green
        square1Colors.put("Face R", new Color(  0,  13, 153)); // blue
        square1Colors.put("Face F", new Color(255,  85,   0)); // orange
        square1Colors.put("Face U", new Color(255, 255, 255)); // white
        this.colors.put("SQUARE-1", square1Colors);

        // 7x7x7 cube
        HashMap<String, Color> vCube7Colors = new HashMap<String, Color>();
        vCube7Colors.put("Face L", new Color(255,  85,   0)); // orange
        vCube7Colors.put("Face B", new Color(  0,  13, 153)); // blue
        vCube7Colors.put("Face D", new Color(255, 234,   0)); // yellow
        vCube7Colors.put("Face R", new Color(212,  17,  17)); // red
        vCube7Colors.put("Face F", new Color(  0, 153,   0)); // green
        vCube7Colors.put("Face U", new Color(255, 255, 255)); // white
        this.colors.put("7x7x7-CUBE", vCube7Colors);

        // default colors
        this.defaultColors = new HashMap<String, HashMap<String,Color>>();
        for (String puzzleId : this.colors.keySet()) {
            this.defaultColors.put(puzzleId, new HashMap<String, Color>());
            for (String faceId : this.colors.get(puzzleId).keySet()) {
                this.defaultColors.get(puzzleId).put(faceId, this.colors.get(puzzleId).get(faceId));
            }
        }
     }

    public HashMap<String, Color> getColors(String puzzleId) {
        return this.colors.get(puzzleId);
    }

    public void setColor(String puzzleId, String faceId, Color color) {
        this.colors.get(puzzleId).put(faceId, color);

        for (ColorListener listener : this.listeners) {
            listener.colorUpdated(puzzleId, faceId, color);
            listener.colorSchemeUpdated(puzzleId, this.colors.get(puzzleId));
        }
    }

    public void setDefaultColor(String puzzleId, String faceId) {
        Color defaultColor = this.defaultColors.get(puzzleId).get(faceId);
        this.colors.get(puzzleId).put(faceId, defaultColor);

        for (ColorListener listener : this.listeners) {
            listener.colorUpdated(puzzleId, faceId, defaultColor);
            listener.colorSchemeUpdated(puzzleId, this.colors.get(puzzleId));
        }
    }

    public void addColorListener(ColorListener listener) {
        this.listeners.add(listener);
    }

    public void removeColorListener(ColorListener listener) {
        this.listeners.remove(listener);
    }
}

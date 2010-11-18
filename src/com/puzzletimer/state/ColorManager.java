package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.ColorScheme.FaceColor;

public class ColorManager {
    private ArrayList<ColorListener> listeners;
    private HashMap<String, ColorScheme> colorSchemeMap;

    public ColorManager(ColorScheme[] colorSchemes) {
        this.listeners = new ArrayList<ColorListener>();

        this.colorSchemeMap = new HashMap<String, ColorScheme>();
        for (ColorScheme colorScheme : colorSchemes) {
            this.colorSchemeMap.put(colorScheme.getPuzzleId(), colorScheme);
        }
    }

    public ColorScheme getColorScheme(String puzzleId) {
        if (this.colorSchemeMap.containsKey(puzzleId)) {
            return this.colorSchemeMap.get(puzzleId);
        }

        return new ColorScheme(puzzleId, new FaceColor[0]);
    }

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorSchemeMap.put(colorScheme.getPuzzleId(), colorScheme);
        for (ColorListener listener : this.listeners) {
            listener.colorSchemeUpdated(colorScheme);
        }
    }

    public void addColorListener(ColorListener listener) {
        this.listeners.add(listener);
    }

    public void removeColorListener(ColorListener listener) {
        this.listeners.remove(listener);
    }
}

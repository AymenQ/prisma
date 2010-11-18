package com.puzzletimer.models;

import java.awt.Color;

public class ColorScheme {
    public static class FaceColor {
        private String faceId;
        private String faceDescription;
        private Color defaultColor;
        private Color color;

        public FaceColor(String faceId, String faceDescription, Color defaultColor, Color color) {
            this.faceId = faceId;
            this.faceDescription = faceDescription;
            this.defaultColor = defaultColor;
            this.color = color;
        }

        public String getFaceId() {
            return this.faceId;
        }

        public String getFaceDescription() {
            return this.faceDescription;
        }

        public Color getDefaultColor() {
            return this.defaultColor;
        }

        public Color getColor() {
            return this.color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

    private String puzzleId;
    private FaceColor[] faceColors;

    public ColorScheme(String puzzleId, FaceColor[] faceColors) {
        this.puzzleId = puzzleId;
        this.faceColors = faceColors;
    }

    public String getPuzzleId() {
        return this.puzzleId;
    }

    public FaceColor[] getFaceColors() {
        return this.faceColors;
    }

    public FaceColor getFaceColor(String faceId) {
        for (FaceColor faceColor : this.faceColors) {
            if (faceColor.getFaceId().equals(faceId)) {
                return faceColor;
            }
        }

        return null;
    }
}

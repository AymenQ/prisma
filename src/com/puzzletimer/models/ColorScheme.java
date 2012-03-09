package com.puzzletimer.models;

import static com.puzzletimer.Internationalization._;

import java.awt.Color;

public class ColorScheme {
    public static class FaceColor {
        private final String puzzleId;
        private final String faceId;
        private final Color defaultColor;
        private final Color color;

        public FaceColor(String puzzleId, String faceId, Color defaultColor, Color color) {
            this.puzzleId = puzzleId;
            this.faceId = faceId;
            this.defaultColor = defaultColor;
            this.color = color;
        }

        public String getPuzzleId() {
            return this.puzzleId;
        }

        public String getFaceId() {
            return this.faceId;
        }

        public String getFaceDescription() {
            return _("face." + this.puzzleId + "." + this.faceId);
        }

        public Color getDefaultColor() {
            return this.defaultColor;
        }

        public FaceColor setColorToDefault() {
            return new FaceColor(
                this.puzzleId,
                this.faceId,
                this.defaultColor,
                this.defaultColor);
        }

        public Color getColor() {
            return this.color;
        }

        public FaceColor setColor(Color color) {
            return new FaceColor(
                this.puzzleId,
                this.faceId,
                this.defaultColor,
                color);
        }
    }

    private final String puzzleId;
    private final FaceColor[] faceColors;

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

    public ColorScheme setFaceColor(FaceColor faceColor) {
        FaceColor[] faceColors = new FaceColor[this.faceColors.length];
        for (int i = 0; i < faceColors.length; i++) {
            faceColors[i] = this.faceColors[i];
            if (this.faceColors[i].getFaceId().equals(faceColor.getFaceId())) {
                faceColors[i] = faceColor;
            }
        }

        return new ColorScheme(this.puzzleId, faceColors);
    }
}

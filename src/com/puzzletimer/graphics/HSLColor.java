package com.puzzletimer.graphics;

import java.awt.Color;

public class HSLColor {
    public int hue;
    public int saturation;
    public int luminance;

    public HSLColor(int hue, int saturation, int luminance) {
        this.hue = hue;
        this.saturation = saturation;
        this.luminance = luminance;
    }

    public Color toColor() {
        float h = hue / 360f;
        float s = saturation / 100f;
        float l = luminance / 100f;

        float q = l < 0.5f ? l * (1f + s) : l + s - (l * s);
        float p = 2f * l - q;

        float tr = h + 1f / 3f;
        tr = tr < 0f ? tr + 1f : tr;
        tr = tr > 1f ? tr - 1f : tr;

        float tg = h;
        tg = tg < 0f ? tg + 1f : tg;
        tg = tg > 1f ? tg - 1f : tg;

        float tb = h - 1f / 3f;
        tb = tb < 0f ? tb + 1f : tb;
        tb = tb > 1f ? tb - 1f : tb;

        float r = 0f;
        if (tr < 1f / 6f)
            r = p + ((q - p) * 6f * tr);
        else if (tr < 0.5f)
            r = q;
        else if (tr < 2f / 3f)
            r = p + ((q - p) * 6f * (2f / 3f - tr));
        else
            r = p;

        float g = 0f;
        if (tg < 1f / 6f)
            g = p + ((q - p) * 6f * tg);
        else if (tg < 0.5f)
            g = q;
        else if (tg < 2f / 3f)
            g = p + ((q - p) * 6f * (2f / 3f - tg));
        else
            g = p;

        float b = 0f;
        if (tb < 1f / 6f)
            b = p + ((q - p) * 6f * tb);
        else if (tb < 0.5f)
            b = q;
        else if (tb < 2f / 3f)
            b = p + ((q - p) * 6f * (2f / 3f - tb));
        else
            b = p;

        return new Color(r, g, b);
    }
}

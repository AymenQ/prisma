package com.puzzletimer.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class HandImage extends JComponent {
    private final GeneralPath path;
    private final boolean mirrored;
    private boolean pressed;

    public HandImage(boolean mirrored) {
        this.path = new GeneralPath();
        this.path.moveTo( 0.00400, -0.43600);
        this.path.curveTo(-0.01886, -0.43507, -0.03426, -0.41220, -0.03580, -0.39080);
        this.path.curveTo(-0.03716, -0.34382, -0.03526, -0.29672, -0.03545, -0.24970);
        this.path.curveTo(-0.03474, -0.18284, -0.03496, -0.11581, -0.03370, -0.04904);
        this.path.curveTo(-0.02907, -0.04205, -0.01845, -0.04763, -0.01160, -0.04599);
        this.path.curveTo( 0.00706, -0.04651,  0.02592, -0.04443,  0.04445, -0.04504);
        this.path.curveTo( 0.05201, -0.05002,  0.04626, -0.06156,  0.04795, -0.06899);
        this.path.curveTo( 0.04676, -0.17803,  0.04662, -0.28714,  0.04480, -0.39615);
        this.path.curveTo( 0.04317, -0.41688,  0.02640, -0.43762,  0.00400, -0.43600);
        this.path.moveTo(-0.08875, -0.41264);
        this.path.curveTo(-0.11117, -0.41088, -0.12302, -0.38723, -0.12520, -0.36729);
        this.path.curveTo(-0.12761, -0.33536, -0.12470, -0.30320, -0.12520, -0.27120);
        this.path.curveTo(-0.12404, -0.19332, -0.12388, -0.11513, -0.12210, -0.03745);
        this.path.curveTo(-0.11759, -0.02980, -0.10709, -0.03779, -0.10040, -0.03720);
        this.path.curveTo(-0.08290, -0.04107, -0.06477, -0.04184, -0.04740, -0.04535);
        this.path.curveTo(-0.04138, -0.05260, -0.04698, -0.06376, -0.04530, -0.07235);
        this.path.curveTo(-0.04717, -0.17121, -0.04815, -0.27011, -0.05055, -0.36896);
        this.path.curveTo(-0.05214, -0.38941, -0.06522, -0.41386, -0.08875, -0.41266);
        this.path.moveTo( 0.09765, -0.38291);
        this.path.curveTo( 0.07647, -0.38192,  0.06463, -0.35949,  0.06545, -0.34021);
        this.path.curveTo( 0.06528, -0.24229,  0.06437, -0.14427,  0.06475, -0.04642);
        this.path.curveTo( 0.06893, -0.03894,  0.08019, -0.04321,  0.08715, -0.04077);
        this.path.curveTo( 0.10364, -0.03961,  0.12025, -0.03607,  0.13665, -0.03567);
        this.path.curveTo( 0.14396, -0.04043,  0.13804, -0.05177,  0.13995, -0.05892);
        this.path.curveTo( 0.13958, -0.15312,  0.14041, -0.24738,  0.13930, -0.34157);
        this.path.curveTo( 0.13750, -0.36297,  0.12084, -0.38429,  0.09765, -0.38292);
        this.path.moveTo(-0.17390, -0.32535);
        this.path.curveTo(-0.19753, -0.32337, -0.21106, -0.29832, -0.21200, -0.27670);
        this.path.curveTo(-0.21116, -0.18590, -0.20947, -0.09502, -0.20770, -0.00426);
        this.path.curveTo(-0.20498,  0.00413, -0.19471, -0.00299, -0.19010, -0.00541);
        this.path.curveTo(-0.17334, -0.01414, -0.15531, -0.02070, -0.13810, -0.02846);
        this.path.curveTo(-0.13328, -0.03587, -0.13813, -0.04623, -0.13665, -0.05461);
        this.path.curveTo(-0.13835, -0.13384, -0.13890, -0.21317, -0.14130, -0.29235);
        this.path.curveTo(-0.14284, -0.30934, -0.15508, -0.32687, -0.17390, -0.32535);
        this.path.moveTo(-0.00720, -0.02516);
        this.path.curveTo(-0.04566, -0.02380, -0.08440, -0.02059, -0.12175, -0.01126);
        this.path.curveTo(-0.17324,  0.00348, -0.21256,  0.04874, -0.22355,  0.10084);
        this.path.curveTo(-0.23413,  0.14816, -0.22547,  0.19739, -0.21505,  0.24399);
        this.path.curveTo(-0.20498,  0.28118, -0.19357,  0.32031, -0.16665,  0.34869);
        this.path.curveTo(-0.15511,  0.36155, -0.13597,  0.36703, -0.11990,  0.35979);
        this.path.curveTo(-0.09503,  0.35282, -0.07897,  0.32912, -0.07495,  0.30459);
        this.path.curveTo(-0.06463,  0.25586, -0.07622,  0.20405, -0.05925,  0.15669);
        this.path.curveTo(-0.04911,  0.12664, -0.02044,  0.10575,  0.01065,  0.10244);
        this.path.curveTo( 0.03577,  0.09797,  0.06099,  0.10389,  0.08605,  0.10524);
        this.path.curveTo( 0.11284,  0.10622,  0.14057,  0.08999,  0.14705,  0.06299);
        this.path.curveTo( 0.15685,  0.02997,  0.13533, -0.00831,  0.10165, -0.01631);
        this.path.curveTo( 0.06596, -0.02299,  0.02919, -0.02547, -0.00720, -0.02516);
        this.path.moveTo( 0.34205,  0.04119);
        this.path.curveTo( 0.31289,  0.04284,  0.28613,  0.05904,  0.26930,  0.08244);
        this.path.curveTo( 0.24593,  0.11190,  0.22702,  0.14956,  0.18935,  0.16289);
        this.path.curveTo( 0.17843,  0.16584,  0.17184,  0.15359,  0.16255,  0.15029);
        this.path.curveTo( 0.11164,  0.12130,  0.04120,  0.12236, -0.00380,  0.16229);
        this.path.curveTo(-0.05729,  0.20987, -0.06621,  0.30113, -0.01945,  0.35644);
        this.path.curveTo( 0.00386,  0.38513,  0.04236,  0.39789,  0.07870,  0.39334);
        this.path.curveTo( 0.12655,  0.39102,  0.17234,  0.37106,  0.20775,  0.33914);
        this.path.curveTo( 0.23828,  0.31164,  0.26692,  0.28069,  0.28865,  0.24554);
        this.path.curveTo( 0.31593,  0.19899,  0.33176,  0.14465,  0.36960,  0.10504);
        this.path.curveTo( 0.38301,  0.08995,  0.38743,  0.06296,  0.37000,  0.04909);
        this.path.curveTo( 0.36220,  0.04282,  0.35187,  0.04075,  0.34205,  0.04119);

        this.mirrored = mirrored;
        this.pressed = false;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        if (this.pressed) {
            size *= 0.95;
        }

        g2.setColor(new Color(240, 211, 16));
        g2.fillOval(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size);

        GeneralPath path = new GeneralPath(this.path);
        path.transform(AffineTransform.getScaleInstance((this.mirrored ? -1.0 : 1.0) * size, size));
        path.transform(AffineTransform.getTranslateInstance(getWidth() / 2, getHeight() / 2));
        g2.setColor(new Color(0, 0, 0));
        g2.fill(path);
    }
}

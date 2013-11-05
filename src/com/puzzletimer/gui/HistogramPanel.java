package com.puzzletimer.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.puzzletimer.Main;
import com.puzzletimer.models.Solution;
import com.puzzletimer.state.ConfigurationManager;
import com.puzzletimer.util.SolutionUtils;

@SuppressWarnings("serial")
public class HistogramPanel extends JPanel {
    private long[] bins;
    private long intervalStart;
    private long intervalEnd;
    private ConfigurationManager configurationManager;

    public HistogramPanel(Solution[] solutions, int nBins, ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    	setBackground(Color.WHITE);

        this.bins = new long[nBins];
        setSolutions(solutions);
    }

    public void setSolutions(Solution[] solutions) {
        // apply +2, filter DNF
        long[] times = SolutionUtils.realTimes(solutions, true, this.configurationManager.getConfiguration("TIMER-PRECISION").equals("CENTISECONDS"));

        // define interval size
        if (times.length == 0) {
            this.intervalStart = 17000;
            this.intervalEnd = 23000;
        } else {
            // mean
            long mean = 0;
            for (int i = 0; i < times.length; i++) {
                mean += times[i];
            }
            mean /= times.length;

            // standard deviation
            long variance = 0;
            for (int i = 0; i < times.length; i++) {
                variance += Math.pow(times[i] - mean, 2d);
            }
            variance /= times.length;

            long stddev = (long) Math.sqrt(variance);

            this.intervalStart = mean - 3 * Math.max(50, stddev);
            this.intervalEnd = mean + 3 * Math.max(50, stddev);
        }

        // calculate histogram
        for (int i = 0; i < this.bins.length; i++) {
            this.bins[i] = 0;
        }

        for (int i = 0; i < times.length; i++) {
            if (times[i] >= this.intervalStart && times[i] < this.intervalEnd) {
                int bin = (int) (this.bins.length * (times[i] - this.intervalStart) / (this.intervalEnd - this.intervalStart));
                this.bins[bin]++;
            }
        }

        // repaint
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setFont(new Font("Arial", Font.BOLD, 10));

        // draw line
        int hBase = 16;
        g2.drawLine(0, getHeight() - hBase, getWidth() - 1, getHeight() - hBase);

        // draw line ticks
        double wBar = (double) getWidth() / (this.bins.length + 1);
        for (int i = 0; i < this.bins.length + 1; i++) {
            int x = (int) ((i + 0.5) * wBar);
            int y = getHeight() - hBase;

            g2.drawLine(x, y - 2, x, y + 2);
        }

        // draw labels
        double binInterval = (double) (this.intervalEnd - this.intervalStart) / this.bins.length;
        for (int i = 0; i < this.bins.length + 1; i++) {
            long value = (long) (this.intervalStart + i * binInterval);
            String label = SolutionUtils.format(value, this.configurationManager.getConfiguration("TIMER-PRECISION"), false);

            FontMetrics fontMetrics = g2.getFontMetrics();
            int width = fontMetrics.stringWidth(label);
            int height = fontMetrics.getAscent();
            int x = (int) ((i + 0.5) * wBar - width / 2);
            int y = getHeight() - (hBase - height) / 2;

            g2.drawString(label, x, y);
        }

        // draw bars
        long maxValue = 0L;
        for (int i = 0; i < this.bins.length; i++) {
            if (this.bins[i] > maxValue) {
                maxValue = this.bins[i];
            }
        }

        if (maxValue > 0) {
            for (int i = 0; i < this.bins.length; i++) {
                int x1 = (int) ((i + 0.5) * wBar);
                int x2 = (int) ((i + 1.5) * wBar);
                int y = getHeight() - hBase;
                int height = (int) (this.bins[i] * (getHeight() - hBase - 4) / maxValue);

                g2.drawRect(x1, y - height, x2 - x1, height);
            }
        }
    }
}

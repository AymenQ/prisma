package com.puzzletimer.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;

import com.puzzletimer.models.Solution;
import com.puzzletimer.state.ConfigurationManager;
import com.puzzletimer.util.SolutionUtils;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel {
    ArrayList<Long> solutionTimes;
    private long solutionIntervalStart;
    private long solutionIntervalEnd;
    ArrayList<Long> startTimes;
    long startIntervalStart;
    long startIntervalEnd;
    private ConfigurationManager configurationManager;

    public GraphPanel(Solution[] solutions, ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
        setBackground(Color.WHITE);
        setSolutions(solutions);
    }

    public void setSolutions(Solution[] solutions) {
        // apply +2, filter DNFs
        this.solutionTimes = new ArrayList<Long>();
        this.startTimes = new ArrayList<Long>();
        for (Solution solution : solutions) {
            long time = SolutionUtils.realTime(solution, this.configurationManager.getConfiguration("TIMER-PRECISION").equals("CENTISECONDS"));
            if (time != Long.MAX_VALUE) {
                this.solutionTimes.add(time);
                this.startTimes.add(solution.getTiming().getStart().getTime());
            }
        }

        // define solution times interval size
        if (this.solutionTimes.size() == 0) {
            this.solutionIntervalStart = 17000;
            this.solutionIntervalEnd = 23000;
        } else {
            // mean
            long mean = 0;
            for (int i = 0; i < this.solutionTimes.size(); i++) {
                mean += this.solutionTimes.get(i);
            }
            mean /= this.solutionTimes.size();

            // standard deviation
            long variance = 0;
            for (int i = 0; i < this.solutionTimes.size(); i++) {
                variance += Math.pow(this.solutionTimes.get(i) - mean, 2d);
            }
            variance /= this.solutionTimes.size();

            long standardDeviation = (long) Math.sqrt(variance);

            this.solutionIntervalStart = mean - 3 * Math.max(50, standardDeviation);
            this.solutionIntervalEnd = mean + 3 * Math.max(50, standardDeviation);
        }

        // define start times interval size
        if (this.solutionTimes.size() == 0) {
            Date now = new Date();
            this.startIntervalStart = now.getTime() - 5000;
            this.startIntervalEnd = now.getTime() + 5000;
        } else if (this.solutionTimes.size() == 1) {
            this.startIntervalStart = this.startTimes.get(0) - 5000;
            this.startIntervalEnd = this.startTimes.get(0) + 5000;
        } else {
            this.startIntervalStart = this.startTimes.get(this.startTimes.size() - 1);
            this.startIntervalEnd = this.startTimes.get(0);

            if (this.startIntervalStart == this.startIntervalEnd) {
                this.startIntervalStart = this.startTimes.get(0) - 5000;
                this.startIntervalEnd = this.startTimes.get(0) + 5000;
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

        int hBase = 16;
        int wBase = 45;

        // draw vertical line
        g2.drawLine(wBase, getHeight() - hBase, getWidth() - 1, getHeight() - hBase);

        // draw vertical ticks
        int nVerticalTicks = 5;
        for (int i = 0; i < nVerticalTicks; i++) {
            int x = wBase;
            int y = (int) (getHeight() - hBase - (i + 0.5) * (getHeight() - hBase) / nVerticalTicks);

            g2.drawLine(x - 2, y, x + 2, y);
        }

        // draw vertical labels
        double vTickInterval = (this.solutionIntervalEnd - this.solutionIntervalStart) / nVerticalTicks;
        for (int i = 0; i < nVerticalTicks; i++) {
            long value = (long) (this.solutionIntervalStart + (i + 0.5) * vTickInterval);
            String label = SolutionUtils.format(value, this.configurationManager.getConfiguration("TIMER-PRECISION"), false);

            FontMetrics fontMetrics = g2.getFontMetrics();
            int width = fontMetrics.stringWidth(label);
            int height = fontMetrics.getAscent();
            int x = wBase - width - 4;
            int y = (int) (getHeight() - hBase - (i + 0.5) * (getHeight() - hBase) / nVerticalTicks + height / 2 - 1);

            g2.drawString(label, x, y);
        }

        // draw horizontal line
        g2.drawLine(wBase, 0, wBase, getHeight() - hBase - 1);

        // draw horizontal ticks
        int nHorizontalTicks = 11;
        for (int i = 0; i < nHorizontalTicks; i++) {
            int x = (int) (wBase + (i + 0.5) * (getWidth() - wBase) / nHorizontalTicks);
            int y = getHeight() - hBase;

            g2.drawLine(x, y - 2, x, y + 2);
        }

        // draw horizontal labels
        double hTickInterval = (double) (this.startIntervalEnd - this.startIntervalStart) / nHorizontalTicks;
        for (int i = 0; i < nHorizontalTicks; i++) {
            long value = (long) (this.startIntervalStart + (i + 0.5) * hTickInterval);
            String label;
            if (this.startIntervalEnd - this.startIntervalStart < 24 * 60 * 60 * 1000) {
                label = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(value);
            } else {
                label = DateFormat.getDateInstance(DateFormat.MEDIUM).format(value);
            }

            FontMetrics fontMetrics = g2.getFontMetrics();
            int width = fontMetrics.stringWidth(label);
            int height = fontMetrics.getAscent();
            int x = (int) (wBase + (i + 0.5) * (getWidth() - wBase) / nHorizontalTicks - width / 2);
            int y = getHeight() - (hBase - height) / 2;

            g2.drawString(label, x, y);
        }

        // draw points
        int nBins = getWidth() - wBase;

        ArrayList<ArrayList<Long>> bins = new ArrayList<ArrayList<Long>>(nBins);
        for (int i = 0; i < nBins; i++) {
            bins.add(new ArrayList<Long>());
        }

        for (int i = 0; i < this.solutionTimes.size(); i++) {
            int bin = (int) ((nBins - 1) * (this.startTimes.get(i) - this.startIntervalStart) / (this.startIntervalEnd - this.startIntervalStart));
            bins.get(bin).add(this.solutionTimes.get(i));
        }

        for (int i = 0; i < nBins; i++) {
            if (bins.get(i).size() > 0) {
                long mean = 0;
                for (long time : bins.get(i)) {
                    mean += time;
                }
                mean /= bins.get(i).size();

                if (mean >= this.solutionIntervalStart && mean < this.solutionIntervalEnd) {
                    int x = wBase + i;
                    int y = (int) (getHeight() - hBase - (getHeight() - hBase) * (mean - this.solutionIntervalStart) / (this.solutionIntervalEnd - this.solutionIntervalStart));

                    g2.fillRect(x - 2, y - 2, 5, 5);
                }
            }
        }
    }
}

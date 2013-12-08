package com.puzzletimer.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StackmatGraphPanel extends JPanel {
    ArrayList<Long> solutionTimes;
    ArrayList<Long> startTimes;
    long startIntervalStart;
    long startIntervalEnd;
    byte[] data;
    int offset;
    double period;
    byte[] decodedData;
    boolean hasSixDigits;

    public StackmatGraphPanel() {
        setBackground(Color.WHITE);
    }

    public void setData(byte[] data) {
    	this.data = data;
    	this.offset = offset;
    	this.period = period;
    	this.decodedData = decodedData;
    	
        // repaint
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.BLACK);
        //g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        
        /*System.out.println((double)getWidth()/(double)this.data.length*this.period);
        System.out.println((double)getWidth());
        System.out.println((double)this.data.length);
        System.out.println(this.period);*/
        
        //for(int i = 0; i < (double)this.data.length/this.period/(double)(this.hasSixDigits ? 10 : 9); i++) {
	        //g2.drawLine((int)((double)offset + ((double)i*(double)getWidth()/(double)this.data.length*this.period)*(double)(this.hasSixDigits ? 10 : 9)), 0, (int)((double)offset + ((double)i*(double)getWidth()/(double)this.data.length*this.period*(double)(this.hasSixDigits ? 10 : 9))), getHeight());
        //}
        
        g2.setColor(Color.RED);
        if(this.data != null) {
	        for(int i = 0; i < this.data.length - 1; i++) {
	        	g2.drawLine((int)((double)getWidth()/(double)this.data.length*(double)i), (int)(-(((double)this.data[i] * ((double)getHeight()/(double)(Byte.MAX_VALUE-Byte.MIN_VALUE))))+(double)getHeight()/2), (int)((double)getWidth()/(double)this.data.length*(i+1)), (int)(-(((double)this.data[i+1] * ((double)getHeight()/(double)(Byte.MAX_VALUE-Byte.MIN_VALUE))))+(double)getHeight()/2));
	        }
        }
    }
}

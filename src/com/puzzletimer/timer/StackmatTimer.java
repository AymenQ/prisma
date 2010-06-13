package com.puzzletimer.timer;

import java.util.ArrayList;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;

interface StackmatTimerReaderListener {
    void dataReceived(byte[] data);
}

class StackmatTimerReader implements Runnable {
    private int sampleRate = 8000;
    private TargetDataLine targetDataLine;
    private ArrayList<StackmatTimerReaderListener> listeners;
    private boolean running;

    StackmatTimerReader() {
        this.listeners = new ArrayList<StackmatTimerReaderListener>();
        this.running = false;
    }

    private byte getSample() {
        byte[] buffer = new byte[1];
        this.targetDataLine.read(buffer, 0, buffer.length);
        return buffer[0];
    }

    private byte findBitThreshold(byte[] samples) {
        double lastSpaceClusterMean = Byte.MIN_VALUE;
        double lastMarkClusterMean = Byte.MAX_VALUE;

        for (;;) {
            double spaceClusterMean = 0d;
            int spaceClusterSize = 0;
            double markClusterMean = 0d;
            int markClusterSize = 0;

            for (byte sample : samples) {
                if (Math.abs(sample - lastSpaceClusterMean) < Math.abs(sample - lastMarkClusterMean)) {
                    spaceClusterMean += sample;
                    spaceClusterSize++;
                } else {
                    markClusterMean += sample;
                    markClusterSize++;
                }
            }

            spaceClusterMean /= spaceClusterSize;
            markClusterMean /= markClusterSize;

            if (Math.abs(lastSpaceClusterMean - spaceClusterMean) <= 1d && Math.abs(lastMarkClusterMean - markClusterMean) <= 1d) {
                break;
            }

            lastSpaceClusterMean = spaceClusterMean;
            lastMarkClusterMean = markClusterMean;
        }

        return (byte) ((lastSpaceClusterMean + lastMarkClusterMean) / 2d);
    }

    private byte[] readPacket(byte bitThreshold) {
        double period = 1d / (this.sampleRate / 1200d); // (sampleRate / baudRate) ** -1
        double t = 0d;

        // alignment
        t = 0.5d;
        while (t > period) {
            getSample();
            t -= period;
        }

        byte[] data = new byte[9];
        for (int i = 0; i < 9; i++) {
            // start bit
            t += 1d;
            while (t > period) {
                getSample();
                t -= period;
            }

            // data bits
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                if (getSample() > bitThreshold) {
                    b |= 0x01 << j;
                }
                t -= period;

                t += 1d;
                while (t > period) {
                    getSample();
                    t -= period;
                }
            }
            data[i] = (byte) ~b;

            // stop bit
            t += 1d;
            while (t > period) {
                getSample();
                t -= period;
            }
        }

        return data;
    }

    @Override
    public void run() {
        this.running = true;

        final AudioFormat format = new AudioFormat(
            this.sampleRate, // sample
            8, // size in bits
            1, // channels
            true, // signed
            false // big endian
        );

        try {
            this.targetDataLine = (TargetDataLine) AudioSystem.getLine(new Info(TargetDataLine.class, format));
            this.targetDataLine.open(format);
        } catch (LineUnavailableException e) {
            throw new RuntimeException();
        }

        this.targetDataLine.start();

        byte[] calibrationSamples = new byte[this.sampleRate / 4];
        this.targetDataLine.read(calibrationSamples, 0, calibrationSamples.length);

        byte bitThreshold = findBitThreshold(calibrationSamples);

        while (this.running) {
            // skip space samples
            while (getSample() <= bitThreshold) {
            }

            byte[] data = readPacket(bitThreshold);

            for (StackmatTimerReaderListener listener : this.listeners) {
                listener.dataReceived(data);
            }
        }

        this.targetDataLine.close();
    }

    public void stop() {
        this.running = false;
    }

    public void addEventListener(StackmatTimerReaderListener listener) {
        this.listeners.add(listener);
    }

    public void removeEventListener(StackmatTimerReaderListener listener) {
        this.listeners.remove(listener);
    }
}

public class StackmatTimer implements StackmatTimerReaderListener, Timer {
    private StackmatTimerReader stackmatTimerReader;
    private ArrayList<TimerListener> listeners;

    public StackmatTimer() {
        this.stackmatTimerReader = new StackmatTimerReader();
        this.listeners = new ArrayList<TimerListener>();

        this.stackmatTimerReader.addEventListener(this);
    }

    private boolean isValidChecksum(byte[] data) {
        int sum = 0;
        for (int i = 1; i < 6; i++) {
            sum += data[i] - '0';
        }

        return sum + 64 == data[6];
    }

    @Override
    public void dataReceived(byte[] data) {
        // checksum
        if (!isValidChecksum(data)) {
            return;
        }

        // hands status
        for (TimerListener listener : this.listeners) {
            if (data[0] == 'A' || data[0] == 'C' || data[0] == 'L') {
                listener.leftHandPressed();
            }

            if (data[0] == ' ' || data[0] == 'I' || data[0] == 'R' || data[0] == 'S') {
                listener.leftHandReleased();
            }

            if (data[0] == 'A' || data[0] == 'C' || data[0] == 'R') {
                listener.rightHandPressed();
            }

            if (data[0] == ' ' || data[0] == 'I' || data[0] == 'L' || data[0] == 'S') {
                listener.rightHandReleased();
            }
        }

        // time
        int minutes = data[1] - '0';
        int seconds = 10 * (data[2] - '0') + data[3] - '0';
        int centiseconds = 10 * (data[4] - '0') + data[5] - '0';

        long time = 60000 * minutes + 1000 * seconds + 10 * centiseconds;
        Date end = new Date();
        Date start = new Date(end.getTime() - time);
        Timing timing = new Timing(start, end);

        // timer ready
        if (data[0] == 'A' || data[0] == 'I') {
            for (TimerListener listener : StackmatTimer.this.listeners) {
                listener.timerReady();
            }
        }

        // timer running
        else if (data[0] == ' ' || data[0] == 'L' || data[0] == 'R') {
            for (TimerListener listener : StackmatTimer.this.listeners) {
                listener.timerRunning(timing);
            }
        }

        // timer stopped
        else if (data[0] == 'S') {
            for (TimerListener listener : StackmatTimer.this.listeners) {
                listener.timerStopped(timing);
            }
        }
    }

    @Override
    public void start() {
        Thread readerThread = new Thread(this.stackmatTimerReader);
        readerThread.start();
    }

    @Override
    public void stop() {
        this.stackmatTimerReader.removeEventListener(this);
        this.stackmatTimerReader.stop();
    }

    @Override
    public void addEventListener(TimerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeEventListener(TimerListener listener) {
        this.listeners.remove(listener);
    }
}

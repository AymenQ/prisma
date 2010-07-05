// reference: http://hackvalue.de/hv_atmel_stackmat

package com.puzzletimer.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

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
    private double period = this.sampleRate / 1200d;
    private TargetDataLine targetDataLine;
    private ArrayList<StackmatTimerReaderListener> listeners;
    private boolean running;

    StackmatTimerReader() {
        this.listeners = new ArrayList<StackmatTimerReaderListener>();
        this.running = false;
    }

    private byte[] readPacket(byte[] samples, int offset, byte bitThreshold) {
        byte[] data = new byte[9];
        for (int i = 0; i < 9; i++) {
            // start bit
            if (samples[offset + (int) (10 * i * this.period)] <= bitThreshold) {
                return new byte[9]; // invalid data
            }

            // data bits
            data[i] = 0x00;
            for (int j = 0; j < 8; j++) {
                if (samples[offset + (int) ((10 * i + j + 1) * this.period)] > bitThreshold) {
                    data[i] |= 0x01 << j;
                }
            }

            // stop bit
            if (samples[offset + (int) ((10 * i + 9) * this.period)] > bitThreshold) {
                return new byte[9]; // invalid data
            }
        }

        return data;
    }

    private boolean isValidPacket(byte[] data) {
        int sum = 0;
        for (int i = 1; i < 6; i++) {
            sum += data[i] - '0';
        }

        return " ACILRS".contains(String.valueOf((char) data[0])) &&
               Character.isDigit(data[1]) &&
               Character.isDigit(data[2]) &&
               Character.isDigit(data[3]) &&
               Character.isDigit(data[4]) &&
               Character.isDigit(data[5]) &&
               data[6] == sum + 64 &&
               data[7] == '\n' &&
               data[8] == '\r';
    }

    private byte[] inverted(byte[] data) {
        byte[] invertedData = new byte[data.length];
        for (int i = 0; i < invertedData.length; i++) {
            invertedData[i] = (byte) ~data[i];
        }
        return invertedData;
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

        byte[] buffer = new byte[this.sampleRate / 4];
        int offset = buffer.length;

        while (this.running) {
            // update buffer in a queue fashion
            for (int i = offset; i < buffer.length; i++) {
                buffer[i - offset] = buffer[i];
            }
            this.targetDataLine.read(buffer, buffer.length - offset, offset);

            boolean isPacketStart = false;
            boolean isSignalInverted = false;

            // find packet start
            loop: for (offset = 0; offset + 0.119171 * this.sampleRate < buffer.length; offset++) {
                for (int threshold = 0; threshold < 256; threshold++) {
                    byte[] data = readPacket(buffer, offset, (byte) (threshold - 127));
                    if (isValidPacket(data)) {
                        isPacketStart = true;
                        break loop;
                    }

                    // try inverting the signal
                    if (isValidPacket(inverted(data))) {
                        isPacketStart = true;
                        isSignalInverted = true;
                        break loop;
                    }
                }
            }

            if (!isPacketStart) {
                continue;
            }

            // create packet histogram
            HashMap<Long, Integer> packetHistogram = new HashMap<Long, Integer>();
            for (int i = 0; i < this.period; i++) {
                for (int threshold = 0; threshold < 256; threshold++) {
                    byte data[] = readPacket(buffer, offset + i, (byte) (threshold - 127));
                    if (isSignalInverted) {
                        data = inverted(data);
                    }

                    if (isValidPacket(data)) {
                        // encode packet
                        long packet = 0L;
                        for (int j = 0; j < 6; j++) {
                            packet |= (long) data[j] << 8 * j;
                        }

                        if (packetHistogram.containsKey(packet)) {
                            packetHistogram.put(packet, packetHistogram.get(packet) + 1);
                        } else {
                            packetHistogram.put(packet, 1);
                        }
                    }
                }
            }

            // select packet with highest frequency
            long packet = 0L;
            int highestFrequency = 0;
            for (Entry<Long, Integer> entry : packetHistogram.entrySet()) {
                if (entry.getValue() > highestFrequency) {
                    packet = entry.getKey();
                    highestFrequency = entry.getValue();
                }
            }

            // decode packet
            byte[] data = new byte[9];
            for (int i = 0; i < 6; i++) {
                data[i] = (byte) (packet >> 8 * i);
            }

            // notify listeners
            for (StackmatTimerReaderListener listener : this.listeners) {
                listener.dataReceived(data);
            }

            // skip read packet
            offset += 0.119171 * this.sampleRate;
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

    @Override
    public void dataReceived(byte[] data) {
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

// reference: http://hackvalue.de/hv_atmel_stackmat

package com.puzzletimer.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.sound.sampled.TargetDataLine;

import com.puzzletimer.models.Timing;
import com.puzzletimer.state.TimerManager;

interface StackmatTimerReaderListener {
    void dataReceived(byte[] data);
}

class StackmatTimerReader implements Runnable {
    private double sampleRate;
    private double period;
    private TargetDataLine targetDataLine;
    private ArrayList<StackmatTimerReaderListener> listeners;
    private boolean running;

    StackmatTimerReader(TargetDataLine targetDataLine) {
        this.sampleRate = targetDataLine.getFormat().getFrameRate();
        this.period = this.sampleRate / 1200d;
        this.targetDataLine = targetDataLine;
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

        this.targetDataLine.start();

        byte[] buffer = new byte[(int) (this.sampleRate / 4)];
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
    private TimerManager timerManager;
    private StackmatTimerReader stackmatTimerReader;
    private Date timingStart;
    private java.util.Timer repeater;

    public StackmatTimer(TimerManager timerManager, TargetDataLine targetDataLine) {
        this.timerManager = timerManager;
        this.stackmatTimerReader = new StackmatTimerReader(targetDataLine);
        this.timingStart = null;

        this.stackmatTimerReader.addEventListener(this);

        this.repeater = new java.util.Timer();
        this.repeater.schedule(new TimerTask() {
            @Override
            public void run() {
                if (StackmatTimer.this.timingStart != null) {
                    Timing timing = new Timing(StackmatTimer.this.timingStart, new Date());
                    StackmatTimer.this.timerManager.timerRunning(timing);
                }
            }
        }, 0, 5);
    }

    @Override
    public String getTimerId() {
        return "STACKMAT-TIMER";
    }

    @Override
    public void dataReceived(byte[] data) {
        // hands status
        if (data[0] == 'A' || data[0] == 'C' || data[0] == 'L') {
            this.timerManager.leftHandPressed();
        }

        if (data[0] == ' ' || data[0] == 'I' || data[0] == 'R' || data[0] == 'S') {
            this.timerManager.leftHandReleased();
        }

        if (data[0] == 'A' || data[0] == 'C' || data[0] == 'R') {
            this.timerManager.rightHandPressed();
        }

        if (data[0] == ' ' || data[0] == 'I' || data[0] == 'L' || data[0] == 'S') {
            this.timerManager.rightHandReleased();
        }

        // time
        int minutes = data[1] - '0';
        int seconds = 10 * (data[2] - '0') + data[3] - '0';
        int centiseconds = 10 * (data[4] - '0') + data[5] - '0';

        long time = 60000 * minutes + 1000 * seconds + 10 * centiseconds;
        Date end = new Date();
        Date start = new Date(end.getTime() - time);
        Timing timing = new Timing(start, end);

        // timing start
        if (data[0] == ' ') {
            this.timingStart = start;
        } else if (data[0] == 'A' || data[0] == 'I' || data[0] == 'S') {
            this.timingStart = null;
        }

        // timer ready
        if (data[0] == 'A' || data[0] == 'I') {
            this.timerManager.timerReady();
        }

        // timer running
        else if (data[0] == ' ' || data[0] == 'L' || data[0] == 'R') {
            this.timerManager.timerRunning(timing);
        }

        // timer stopped
        else if (data[0] == 'S') {
            this.timerManager.timerStopped(timing);
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
        this.repeater.cancel();
    }
}

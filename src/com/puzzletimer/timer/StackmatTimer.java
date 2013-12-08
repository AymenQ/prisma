// reference: http://hackvalue.de/hv_atmel_stackmat

package com.puzzletimer.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimerTask;

import javax.sound.sampled.TargetDataLine;

import com.puzzletimer.gui.StackmatDeveloperFrame;
import com.puzzletimer.models.Timing;
import com.puzzletimer.state.TimerManager;

interface StackmatTimerReaderListener {
    void dataReceived(byte[] data, boolean hasSixDigits);
}

class StackmatTimerReader implements Runnable {
    private double sampleRate;
    private int baudRateOffset;
    private int previousBaudRate;
    private int baudRate;
    private double period;
    private TargetDataLine targetDataLine;
    private ArrayList<StackmatTimerReaderListener> listeners;
    private boolean running;
    private boolean hasSixDigits;
    private TimerManager timerManager;

    StackmatTimerReader(TargetDataLine targetDataLine, TimerManager timerManager) {
        this.sampleRate = targetDataLine.getFormat().getFrameRate();
        this.baudRateOffset = 0;
        this.previousBaudRate = 1200;
        this.baudRate = this.previousBaudRate + this.baudRateOffset;
        this.period = this.sampleRate / (double) this.baudRate;
        this.targetDataLine = targetDataLine;
        this.listeners = new ArrayList<StackmatTimerReaderListener>();
        this.running = false;
        this.hasSixDigits = false;
        this.timerManager = timerManager;;
    }

    private byte[] readPacket(byte[] samples, int offset, byte bitThreshold, boolean isInverted) {
        byte[] data = new byte[10];
        for (int i = 0; i < 9; i++) {
            // start bit
            boolean startBit = samples[offset + (int) (10 * i * this.period)] <= bitThreshold;
            if ((isInverted && startBit) || (!isInverted && !startBit)) {
                return new byte[10]; // invalid data
            }

            // data bits
            data[i] = 0x00;
            for (int j = 0; j < 8; j++) {
                if (samples[offset + (int) ((10 * i + j + 1) * this.period)] > bitThreshold) {
                    data[i] |= 0x01 << j;
                }
            }

            if (isInverted) {
                data[i] = (byte) ~data[i];
            }

            // stop bit
            boolean stopBit = samples[offset + (int) ((10 * i + 9) * this.period)] <= bitThreshold;
            if ((isInverted && !stopBit) || (!isInverted && stopBit)) {
                return new byte[10]; // invalid data
            }
        }
        if(data[8] == '\n') this.hasSixDigits = true;
        if(data[8] == '\r') this.hasSixDigits = false;
        if(this.hasSixDigits) {
            int i = 9;
            // start bit
            boolean startBit = samples[offset + (int) (10 * i * this.period)] <= bitThreshold;
            if ((isInverted && startBit) || (!isInverted && !startBit)) {
                return new byte[10]; // invalid data
            }

            // data bits
            data[i] = 0x00;
            for (int j = 0; j < 8; j++) {
                if (samples[offset + (int) ((10 * i + j + 1) * this.period)] > bitThreshold) {
                    data[i] |= 0x01 << j;
                }
            }

            if (isInverted) {
                data[i] = (byte) ~data[i];
            }

            // stop bit
            boolean stopBit = samples[offset + (int) ((10 * i + 9) * this.period)] <= bitThreshold;
            if ((isInverted && !stopBit) || (!isInverted && stopBit)) {
                return new byte[10]; // invalid data
            }
        }
        return data;
    }

    private boolean isValidPacket(byte[] data) {
        int sum = 0;
        for (int i = 1; i < (hasSixDigits ? 7 : 6); i++) {
            sum += data[i] - '0';
        }

        return hasSixDigits ? (" ACILRS".contains(String.valueOf((char) data[0])) &&
               Character.isDigit(data[1]) &&
               Character.isDigit(data[2]) &&
               Character.isDigit(data[3]) &&
               Character.isDigit(data[4]) &&
               Character.isDigit(data[5]) &&
               Character.isDigit(data[6]) &&
               data[7] == sum + 64 &&
               data[8] == '\n' &&
               data[9] == '\r') :
               ((" ACILRS".contains(String.valueOf((char) data[0])) &&
               Character.isDigit(data[1]) &&
               Character.isDigit(data[2]) &&
               Character.isDigit(data[3]) &&
               Character.isDigit(data[4]) &&
               Character.isDigit(data[5]) &&
               data[6] == sum + 64 &&
               data[7] == '\n' &&
               data[8] == '\r'));
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
            //System.out.println(buffer.length);
            //System.out.println(offset);
            //System.out.println();

            boolean isPacketStart = false;
            boolean isSignalInverted = false;

            // find packet start
            loop: for(this.baudRateOffset = 0; this.baudRateOffset < 25; this.baudRateOffset++) {
                this.baudRate = this.previousBaudRate + this.baudRateOffset;
                this.period = this.sampleRate / (double) this.baudRate;
                for (offset = 0; offset + (this.hasSixDigits ? 0.132015 : 0.119181) * this.sampleRate < buffer.length; offset++) {
                    for (int threshold = 0; threshold < 256; threshold++) {
                        byte[] data = readPacket(buffer, offset, (byte) (threshold - 127), false);
                        if (isValidPacket(data)) {
                            isPacketStart = true;
                            break loop;
                        }

                        // try inverting the signal
                        data = readPacket(buffer, offset, (byte) (threshold - 127), true);
                        if (isValidPacket(data)) {
                            isPacketStart = true;
                            isSignalInverted = true;
                            break loop;
                        }
                    }
                }
                this.baudRateOffset = -this.baudRateOffset;
                if(this.baudRateOffset < 0) this.baudRateOffset--;
            }

            if (!isPacketStart) {
                this.timerManager.dataNotReceived(buffer);
                System.out.println(false);
                continue;
            }

            HashMap<Integer, Integer> baudRateHistogram = new HashMap<Integer, Integer>();
            for(int i = 0; i < 10; i++) {
                    for (int j = 0; j < this.period; j++) {
                    this.baudRate = this.previousBaudRate + this.baudRateOffset + i;
                    this.period = this.sampleRate / (double) this.baudRate;
                    byte data[] = readPacket(buffer, offset + j, (byte) 0, isSignalInverted);

                    if (isValidPacket(data)) {
                        if (baudRateHistogram.containsKey(this.previousBaudRate + this.baudRateOffset + i)) {
                            baudRateHistogram.put(this.previousBaudRate + this.baudRateOffset + i, baudRateHistogram.get(this.previousBaudRate + this.baudRateOffset + i) + 1);
                        } else {
                            baudRateHistogram.put(this.previousBaudRate + this.baudRateOffset + i, 1);
                        }
                    }
                }
                i = -i;
                if(i < 0) i--;
            }

            int highestFrequencyBaudRate = 0;
            for (Entry<Integer, Integer> entry : baudRateHistogram.entrySet()) {
                if (entry.getValue() > highestFrequencyBaudRate) {
                    this.baudRate = entry.getKey();
                    highestFrequencyBaudRate = entry.getValue();
                }
            }

            this.period = this.sampleRate / this.baudRate;
            this.previousBaudRate = this.baudRate;

            // create packet histogram
            HashMap<Long, Integer> packetHistogram = new HashMap<Long, Integer>();
            for (int i = 0; i < this.period; i++) {
                for (int threshold = 0; threshold < 256; threshold++) {
                    byte data[] = readPacket(buffer, offset + i, (byte) (threshold - 127), isSignalInverted);

                    if (isValidPacket(data)) {
                        // encode packet
                        long packet = 0L;
                        for (int j = 0; j < (hasSixDigits ? 7 : 6); j++) {
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
            for (int i = 0; i < (hasSixDigits ? 7 : 6); i++) {
                data[i] = (byte) (packet >> 8 * i);
            }
            
            this.timerManager.dataNotReceived(buffer);

            // notify listeners
            for (StackmatTimerReaderListener listener : this.listeners) {
                listener.dataReceived(data, this.hasSixDigits);
            }

            // skip read packet
            offset += (this.hasSixDigits ? 0.132015 : 0.119181) * this.sampleRate;
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
    private enum State {
        NOT_READY,
        RESET_FOR_INSPECTION,
        RESET,
        READY,
        RUNNING,
    };

    private StackmatTimerReader stackmatTimerReader;
    private TimerManager timerManager;
    private boolean inspectionEnabled;
    private boolean smoothTimingEnabled;
    private TimerManager.Listener timerListener;
    private java.util.Timer repeater;
    private Date start;
    private State state;
    private long previousTime;

    public StackmatTimer(TargetDataLine targetDataLine, TimerManager timerManager, StackmatDeveloperFrame stackmatDeveloperFrame) {
        this.stackmatTimerReader = new StackmatTimerReader(targetDataLine, timerManager);
        this.timerManager = timerManager;
        this.inspectionEnabled = false;
        this.smoothTimingEnabled = true;
        this.start = null;
        this.state = State.NOT_READY;
        this.previousTime = -1;
    }

    @Override
    public String getTimerId() {
        return "STACKMAT-TIMER";
    }

    @Override
    public void setInspectionEnabled(boolean inspectionEnabled) {
        this.inspectionEnabled = inspectionEnabled;

        switch (this.state) {
            case RESET_FOR_INSPECTION:
                if (!inspectionEnabled) {
                    this.state = State.RESET;
                }
                break;

            case RESET:
                if (inspectionEnabled) {
                    this.state = State.RESET_FOR_INSPECTION;
                }
                break;
        }
    }

    @Override
    public void start() {
        this.timerListener = new TimerManager.Listener() {
            @Override
            public void inspectionFinished() {
                StackmatTimer.this.state = State.NOT_READY;
            }
        };
        this.timerManager.addListener(this.timerListener);

        this.stackmatTimerReader.addEventListener(this);
        Thread readerThread = new Thread(this.stackmatTimerReader);
        readerThread.start();
        
        this.repeater = new java.util.Timer();
        this.repeater.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (StackmatTimer.this.state) {
                    case RUNNING:
                        if(smoothTimingEnabled) StackmatTimer.this.timerManager.updateSolutionTiming(
                            new Timing(StackmatTimer.this.start, new Date()));
                        break;
                }
            }
        }, 0, 5);
    }

    @Override
    public void stop() {
        this.timerManager.removeListener(this.timerListener);

        this.stackmatTimerReader.removeEventListener(this);
        this.stackmatTimerReader.stop();
        
        this.repeater.cancel();
    }

    @Override
    public void dataReceived(byte[] data, boolean hasSixDigits) {
        // hands status
        if (data[0] == 'A' || data[0] == 'L' || data[0] == 'C') {
            this.timerManager.pressLeftHand();
        } else {
            this.timerManager.releaseLeftHand();
        }

        if (data[0] == 'A' || data[0] == 'R' || data[0] == 'C') {
            this.timerManager.pressRightHand();
        } else {
            this.timerManager.releaseRightHand();
        }

        // time
        int minutes = data[1] - '0';
        int seconds = 10 * (data[2] - '0') + data[3] - '0';
        long time;
        if(hasSixDigits) {
        	int milliseconds = 100 * (data[4] - '0') + 10 * (data[5] - '0') + data[6] - '0';
            time = 60000 * minutes + 1000 * seconds + milliseconds;
        } else {
            int centiseconds = 10 * (data[4] - '0') + data[5] - '0';
            time = 60000 * minutes + 1000 * seconds + 10 * centiseconds;
        }

        Date end = new Date();
        Date start = new Date(end.getTime() - time);
        Timing timing = new Timing(start, end);

        this.start = start;

        // state transitions
        switch (this.state) {
            case NOT_READY:
                this.timerManager.updateSolutionTiming(timing);
            
                // timer initialized
                if (time == 0) {
                    this.timerManager.resetTimer();

                    this.state = this.inspectionEnabled ?
                        State.RESET_FOR_INSPECTION : State.RESET;
                }
                break;

            case RESET_FOR_INSPECTION:
                // some pad pressed
                if (data[0] == 'L' || data[0] == 'R' || data[0] == 'C') {
                    this.timerManager.startInspection();

                    this.state = State.RESET;
                }
                break;

            case RESET:
                if(!this.inspectionEnabled)this.timerManager.updateSolutionTiming(timing);
            	
                // ready to start
                if (data[0] == 'A') {
                    this.state = State.READY;
                }

                // timing started
                if (time > 0) {
                    this.timerManager.startSolution();

                    this.state = State.RUNNING;
                }
                break;

            case READY:
                this.timerManager.updateSolutionTiming(timing);
            	
                // timing started
                if (time > 0) {
                    this.timerManager.startSolution();

                    this.state = State.RUNNING;
                }
                break;

            case RUNNING:
                this.timerManager.updateSolutionTiming(timing);
                
                // timer reset during solution
                if (time == 0) {
                    this.state = State.NOT_READY;
                }

                // timer stopped
                if (data[0] == 'S' || time == previousTime) {
                    this.state = State.NOT_READY;

                    this.timerManager.finishSolution(timing);
                }

                break;
        }
        this.previousTime = time;
    }

	@Override
	public void setSmoothTimingEnabled(boolean smoothTimingEnabled) {
        this.smoothTimingEnabled = smoothTimingEnabled;
	}
}
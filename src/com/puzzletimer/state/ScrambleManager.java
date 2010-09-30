package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.scramblers.Scrambler;

public class ScrambleManager {
    private ArrayList<ScrambleListener> listeners;
    private Scrambler currentScrambler;
    private String[] currentSequence;

    public ScrambleManager(Scrambler scrambler) {
        this.listeners = new ArrayList<ScrambleListener>();
        this.currentScrambler = scrambler;
        this.currentSequence = scrambler.getNextScrambleSequence();
    }

    public void setScrambler(Scrambler scrambler) {
        this.currentScrambler = scrambler;
        changeScramble();
    }

    public void changeScramble() {
        this.currentSequence = this.currentScrambler.getNextScrambleSequence();
        notifyListeners();
    }

    public void notifyListeners() {
        for (ScrambleListener listener : this.listeners) {
            listener.scrambleChanged(this.currentSequence);
        }
    }

    public void addScrambleListener(ScrambleListener listener) {
        this.listeners.add(listener);
    }

    public void removeScrambleListener(ScrambleListener listener) {
        this.listeners.remove(listener);
    }
}

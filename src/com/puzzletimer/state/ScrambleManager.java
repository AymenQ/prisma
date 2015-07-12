package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Collections;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;

public class ScrambleManager {
    public static class Listener {
        public void scramblesAdded(Scramble[] scrambles) { }
        public void scramblesRemoved(Scramble[] scrambles) { }
        public void scrambleQueueUpdated(Scramble[] queue) { }
        public void scrambleChanged(Scramble scramble) { }
    }

    private ArrayList<Listener> listeners;
    private ScramblerProvider scramblerProvider;
    private Scrambler currentScrambler;
    private ArrayList<Scramble> queue;
    private Scramble currentScramble;

    public ScrambleManager(ScramblerProvider scramblerProvider, Scrambler scrambler) {
        this.listeners = new ArrayList<>();
        this.scramblerProvider = scramblerProvider;
        this.currentScrambler = scrambler;
        this.queue = new ArrayList<>();
        this.currentScramble = this.currentScrambler.getNextScramble();
    }

    public void setCategory(Category category) {
        this.queue.clear();
        this.currentScrambler = this.scramblerProvider.get(category.getScramblerId());
        changeScramble();
        notifyListeners();
    }

    public Scramble[] getQueue() {
        Scramble[] queueArray = new Scramble[this.queue.size()];
        this.queue.toArray(queueArray);
        return queueArray;
    }

    public void addScrambles(Scramble[] scrambles, boolean priority) {
    	if(priority) {
            for (Scramble scramble : scrambles) {
                this.queue.add(0, scramble);
            }
    	} else {
            Collections.addAll(this.queue, scrambles);
    	}

        for (Listener listener : this.listeners) {
            listener.scramblesAdded(scrambles);
        }

        notifyListeners();
    }

    public void removeScrambles(int[] indices) {
        Scramble[] scrambles = new Scramble[indices.length];
        for (int i = 0; i < scrambles.length; i++) {
            scrambles[i] = this.queue.get(indices[i]);
        }

        for (Scramble scramble : scrambles) {
            this.queue.remove(scramble);
        }

        for (Listener listener : this.listeners) {
            listener.scramblesRemoved(scrambles);
        }

        notifyListeners();
    }

    public void moveScramblesUp(int[] indices) {
        for (int indice : indices) {
            Scramble temp = this.queue.get(indice - 1);
            this.queue.set(indice - 1, this.queue.get(indice));
            this.queue.set(indice, temp);
        }

        notifyListeners();
    }

    public void moveScramblesDown(int[] indices) {
        for (int i = indices.length - 1; i >= 0; i--) {
            Scramble temp = this.queue.get(indices[i] + 1);
            this.queue.set(indices[i] + 1, this.queue.get(indices[i]));
            this.queue.set(indices[i], temp);
        }

        notifyListeners();
    }

    public Scramble getCurrentScramble() {
        return this.currentScramble;
    }

    public void changeScramble() {
        if (this.queue.size() > 0) {
            this.currentScramble = this.queue.get(0);
            removeScrambles(new int[] { 0 });
        } else {
            this.currentScramble = this.currentScrambler.getNextScramble();
        }

        for (Listener listener : this.listeners) {
            listener.scrambleChanged(this.currentScramble);
        }
    }

    private void notifyListeners() {
        Scramble[] queueArray = new Scramble[this.queue.size()];
        this.queue.toArray(queueArray);
        for (Listener listener : this.listeners) {
            listener.scrambleQueueUpdated(queueArray);
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}

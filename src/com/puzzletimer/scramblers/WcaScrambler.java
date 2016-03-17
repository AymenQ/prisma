package com.puzzletimer.scramblers;

import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.ScramblerInfo;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.Random;

public abstract class WcaScrambler implements Scrambler {
    private ScramblerInfo scramblerInfo;
    private Puzzle puzzle;
    private Random random;

    public WcaScrambler(ScramblerInfo scramblerInfo) {
        this.scramblerInfo = scramblerInfo;
        random = new Random();
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public ScramblerInfo getScramblerInfo() {
        return scramblerInfo;
    }

    @Override
    public Scramble getNextScramble() {
        return new Scramble(
                getScramblerInfo().getScramblerId(),
                puzzle.generateWcaScramble(random).split("\\s"));
    }

    @Override
    public String toString() {
        return getScramblerInfo().getDescription();
    }
}

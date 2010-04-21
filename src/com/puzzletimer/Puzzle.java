package com.puzzletimer;

import javax.swing.KeyStroke;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;

public interface Puzzle {
    String getName();
    int getMnemonic();
    KeyStroke getAccelerator();
    boolean isDefaultPuzzle();
    Scrambler getScrambler();
    Mesh getMesh(Scramble scramble);
}

package com.puzzletimer;

import com.puzzletimer.graphics.Mesh;
import com.puzzletimer.scrambles.Scramble;
import com.puzzletimer.scrambles.Scrambler;

public interface Puzzle {
    Scrambler getScrambler();
    Mesh getMesh(Scramble scramble);
}

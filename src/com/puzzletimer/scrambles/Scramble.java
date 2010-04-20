package com.puzzletimer.scrambles;

import java.util.ArrayList;

public class Scramble {
    public ArrayList<Move> moves;
    
    public Scramble(ArrayList<Move> moves) {
        this.moves = moves;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < moves.size() - 1; i++) {
            sb.append(moves.get(i) + "  ");
        }
        
        if (moves.size() > 0) {
            sb.append(moves.get(moves.size() - 1));
        }
        
        return sb.toString();
    }
}

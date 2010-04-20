package com.puzzletimer.scrambles;

public enum MegaminxMove implements Move{
    R2("R++"), R7("R--"),
    D2("D++"), D7("D--"),
    U("U"), U6("U'");
    
    private String description;
    
    private MegaminxMove(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}

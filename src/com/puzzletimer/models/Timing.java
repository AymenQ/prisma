package com.puzzletimer.models;

import java.util.Date;

public class Timing {
    private final Date start;
    private final Date end;

    public Timing(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return this.start;
    }

    public Date getEnd() {
        return this.end;
    }

    public long getElapsedTime() {
        long elapsedTime = this.end == null ?
            new Date().getTime() - this.start.getTime() :
            this.end.getTime() - this.start.getTime();

        return elapsedTime;
    }
}

package com.puzzletimer.timer;

import java.util.Date;

public class Timing {
    private Date start;
    private Date end;

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
        if (this.end != null) {
            return this.end.getTime() - this.start.getTime();
        }

        return new Date().getTime() - this.start.getTime();
    }
}

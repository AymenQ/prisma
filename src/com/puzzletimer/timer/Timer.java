package com.puzzletimer.timer;

import java.util.Date;

public class Timer {
    private Date start, end;

    public long getDiff() {
        Date last = end != null ? end : new Date();
        return last.getTime() - start.getTime();
    }

    public void start() {
        start = new Date();
        end = null;
    }

    public void stop() {
        end = new Date();
    }
    
    public boolean isRunning() {
        return this.start != null && this.end == null;
    }
}

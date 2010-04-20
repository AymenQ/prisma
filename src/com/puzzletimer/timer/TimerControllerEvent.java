package com.puzzletimer.timer;

import java.util.EventObject;

@SuppressWarnings("serial")
public class TimerControllerEvent extends EventObject {
    public TimerControllerEvent(Object source) {
        super(source);
    }
}

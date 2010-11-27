package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MessageManager {
    public enum MessageType {
        INFORMATION,
        ERROR,
    };

    private ArrayList<String> messageQueue;
    private ArrayList<MessageType> messageTypeQueue;
    private ArrayList<MessageListener> listeners;

    public MessageManager() {
        this.messageQueue = new ArrayList<String>();
        this.messageTypeQueue = new ArrayList<MessageType>();
        this.listeners = new ArrayList<MessageListener>();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            private Date nextUpdate = new Date();
            private boolean clear = true;

            @Override
            public void run() {
                Date now = new Date();

                if (now.getTime() > this.nextUpdate.getTime()) {
                    if (!this.clear) {
                        for (MessageListener listener : MessageManager.this.listeners) {
                            listener.clear();
                        }

                        this.clear = true;
                    }


                    synchronized (MessageManager.this) {
                        if (MessageManager.this.messageTypeQueue.size() > 0) {
                            MessageType messageType =
                                MessageManager.this.messageTypeQueue.remove(0);
                            String message =
                                MessageManager.this.messageQueue.remove(0);

                            for (MessageListener listener : MessageManager.this.listeners) {
                                listener.messageReceived(messageType, message);
                            }

                            this.nextUpdate = new Date(now.getTime() + 8000);
                            this.clear = false;
                        }
                    }
                }
            }
        }, 0, 250);
    }

    public void enqueueMessage(MessageType messageType, String message) {
        synchronized (this) {
            this.messageTypeQueue.add(messageType);
            this.messageQueue.add(message);
        }
    }

    public void addMessageListener(MessageListener listener) {
        this.listeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        this.listeners.remove(listener);
    }
}

package com.example.mydfs_back.lib.eventApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventPublisher {
    private List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener){
        listeners.add(listener);
    }
    public void unsubscribe(EventListener listener){
        listeners.remove(listener);
    }
    public void publishEvent(Event event) throws Exception{
        for (EventListener listener: listeners) {
            listener.onEvent(event);
        }
    }
}

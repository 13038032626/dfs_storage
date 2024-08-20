package com.example.mydfs_back.lib.eventApi.listenerImpl;

import com.example.mydfs_back.controller.Dispatcher;
import com.example.mydfs_back.lib.eventApi.Event;
import com.example.mydfs_back.lib.eventApi.EventListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public class followerAsListener implements EventListener {
    Dispatcher dispatcher = new Dispatcher();
    @Override
    public void onEvent(Event event) throws IOException, ExecutionException, InterruptedException {
        dispatcher.dispatch(event);
    }
}

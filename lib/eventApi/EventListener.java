package com.example.mydfs_back.lib.eventApi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface EventListener {
    void onEvent(Event event) throws NoSuchMethodException, IOException, ExecutionException, InterruptedException;
}

package com.example.mydfs_back.lib.eventApi;

import java.util.Map;

public class Event {
    private String message;
    private Map<String,Object> args;
    public Event(String message, Map<String,Object> args){
        this.message = message;
        this.args = args;
    }

    public String getMessage() {
        return message;
    }
    public Object getArg(String name){
        return args.get(name);
    }
}

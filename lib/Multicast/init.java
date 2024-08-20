package com.example.mydfs_back.lib.Multicast;

import com.example.mydfs_back.controller.Dispatcher;
import com.example.mydfs_back.lib.eventApi.Event;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class init {

    public static MulticastSocket multicastSocket;
    public static InetAddress group;

    static {
        try {
            group = InetAddress.getByName("224.0.0.1");
            multicastSocket = new MulticastSocket(8888);
            multicastSocket.joinGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void startListen() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        new Thread(() -> {
            Dispatcher dispatcher = new Dispatcher();
            ObjectMapper mapper = new ObjectMapper();
            while (true) {
                try {
                    multicastSocket.receive(packet);
                    String data = new String(buffer, 0, packet.getLength());
                    Event event = mapper.readValue(data, Event.class);
                    dispatcher.dispatch(event);

                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void sendMessage(String message) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, group, 8888);
        multicastSocket.send(packet);
    }
}

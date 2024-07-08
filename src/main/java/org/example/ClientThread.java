package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    Socket client;
    Server server;
    PrintWriter writer;
    String username;

    public ClientThread(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void run() {
        try {
            InputStream input = client.getInputStream();
            OutputStream output = client.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            writer = new PrintWriter(output, true);
            String rawMessage;
            while((rawMessage = reader.readLine()) != null) {
                Message message = new ObjectMapper().readValue(rawMessage, Message.class);
                switch (message.type) {
                    case Broadcast -> server.broadcast(message);
                    case Login -> login(message.content);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Message message) throws JsonProcessingException {
        String rawMessage = new ObjectMapper().writeValueAsString(message);
        writer.println(rawMessage);
    }

    public void login(String username) throws JsonProcessingException {
        this.username = username;
        Message message = new Message(Message.MessageType.Broadcast, "Użytkownik " + username + "dołączył.");
        send(message);
    }
}

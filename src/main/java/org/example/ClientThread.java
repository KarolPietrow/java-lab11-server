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
                message.username = this.username;
                switch (message.type) {
                    case Broadcast -> {
                            server.broadcast(message);
                    }
                    case DM -> {
                        directMessage(message);
                    }
                    case Login -> {
                    login(message.content);
                    System.out.println("Użytkownik " + message.content + " dołączył.");
                    server.broadcast(new Message(Message.MessageType.Login, "Użytkownik " + message.content + " dołączył."));
                    }
                    case Disconnect -> {
                    server.broadcast(new Message(Message.MessageType.Disconnect, "Użytkownik " + message.content + " opuścił czat."));
                    System.out.println("Użytkownik " + message.content + " opuścił czat.");
                    }
                    case UserList -> userList();
                }
            }
        } catch (IOException e) {} finally {
            try { client.close(); } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Message message) throws JsonProcessingException {
        String rawMessage = new ObjectMapper().writeValueAsString(message);
        writer.println(rawMessage);
    }

    public void login(String username) throws JsonProcessingException {
        this.username = username;
    }

    public void userList() throws JsonProcessingException {
        StringBuilder list = new StringBuilder("Lista aktywnych użytkowników na czacie: ");
        for (ClientThread client : server.getClients()) {
            list.append(client.getUsername()).append("; ");
        }
        send(new Message(Message.MessageType.Broadcast, list.toString(), "SYSTEM"));
    }

    private void directMessage(Message message) throws JsonProcessingException {
        ClientThread recipient = server.getClientUsername(message.recipient);
        if (recipient != null) {
            recipient.send(new Message(Message.MessageType.DM, message.content, username));
            send(new Message(Message.MessageType.DM, "Wiadomość prywatna wysłana", "SYSTEM"));
        } else {
            send(new Message(Message.MessageType.DM, "Nie znaleziono użytkownika " + message.recipient, "SYSTEM"));
        }
    }

}
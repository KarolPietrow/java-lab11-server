package org.example;

public class Message {
    public enum MessageType{
        Broadcast,
        DM,
        Login,
        Disconnect,
        UserList
    }
    public MessageType type;
    public String content;
    public String recipient;
    public String username;

    public Message() {}

    public Message(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public Message(MessageType type, String content, String username) {
        this.type = type;
        this.content = content;
        this.username = username;
    }

    public Message(MessageType type, String content, String recipient, String username) {
        this.type = type;
        this.content = content;
        this.recipient = recipient;
        this.username = username;
    }
}
package org.example;

import java.awt.*;

public class Message {
    public enum MessageType{
        Broadcast,
        Login
    }
    public MessageType type;
    public String content;
    public Message() {}

    public Message(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }
}

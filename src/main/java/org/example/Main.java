package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server(5005);
            server.listen();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
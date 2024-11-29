package org.kunp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class ServerProtocol {
    private BufferedReader in;
    private PrintWriter out;

    public ServerProtocol(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public String refreshRoom(String sessionId, String roomName, int timeLimit, int playerLimit) throws IOException {
        String message = String.format("100|%s|%s|%d|%d|1", sessionId, roomName, timeLimit, playerLimit);
        out.println(message);
        out.flush();
        System.out.println(in.readLine());
        return in.readLine();
    }

    public Set<String> enterRoom(String sessionId, String roomName, int timeLimit, int playerLimit) throws IOException {
        String message = String.format("101|%s|%s|%d|%d|1", sessionId, roomName, timeLimit, playerLimit);
        out.println(message);
        out.flush();

        Set<String> sessionIds = new HashSet<>();
        while ((message = in.readLine()) != null) {
            System.out.println(message);
            String[] parts = message.split("\\|");
            if ("110".equals(parts[0])) {
                String sessionIdInRoom = parts[1];
                sessionIds.add(sessionIdInRoom);
            }
        }
        return sessionIds;
    }

    public void createRoom(String sessionId, String roomName, int timeLimit, int playerLimit) throws IOException {
        String message = String.format("102|%s|%s|%d|%d|1", sessionId, roomName, timeLimit, playerLimit);
        out.println(message);
        out.flush();
        System.out.println(in.readLine());
    }

    public void exitRoom(String sessionId, String roomName, int timeLimit, int playerLimit) throws IOException {
        String message = String.format("103|%s|%s|%d|%d|1", sessionId, roomName, timeLimit, playerLimit);
        out.println(message);
        out.flush();
        System.out.println(in.readLine());
    }

    public String startGame(String sessionId, String roomName, int timeLimit, int playerLimit) throws IOException {
        String message = String.format("105|%s|%s|%d|%d|1", sessionId, roomName, timeLimit, playerLimit);
        out.println(message);
        out.flush();
        System.out.println(in.readLine());
        return in.readLine();
    }
}

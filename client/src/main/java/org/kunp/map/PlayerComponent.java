package org.kunp.map;

import org.kunp.manager.ScreenManager;
import org.kunp.manager.ServerCommunicator;
import org.kunp.manager.StateManager;

import java.io.PrintWriter;

public class PlayerComponent {
    private int x, y;
    private String role;
    private String sessionId;
    private int mapIdx;
    private PrintWriter out;
    private ServerCommunicator serverCommunicator;
    private int gameId;

    public PlayerComponent(StateManager stateManager, ServerCommunicator serverCommunicator, ScreenManager screenManager, int startX, int startY, String role, PrintWriter out, String sessionId, int gameId) {
        this.x = startX;
        this.y = startY;
        this.role = role;
        this.out = out;
        this.sessionId = sessionId;
        this.mapIdx = 5;
        this.serverCommunicator = serverCommunicator;
        this.gameId = gameId;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getRole() {
        return role;
    }
    public String getSessionId() {
        return sessionId;
    }
    public int getMapIdx() {
        return mapIdx;
    }
    public void setMapIdx(int mapIdx) {
        this.mapIdx = mapIdx;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        sendLocation();
    }

    public void sendInteraction(){
        String requestMessage = String.format("202|%s|%s|%d|%d|%d", sessionId, x, y, mapIdx, gameId);
        serverCommunicator.sendRequest(requestMessage);
    }

    private void sendLocation() {
        String requestMessage = String.format("201|%s|%s|%d|%d|%d", sessionId, x, y, mapIdx, gameId);
        serverCommunicator.sendRequest(requestMessage);
    }
}


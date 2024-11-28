package org.kunp.map;

import java.io.PrintWriter;

public class Player {
    private int x, y;
    private String role;
    private String sessionId;
    private int mapIdx;
    private PrintWriter out;

    public Player(int startX, int startY, String role, PrintWriter out, String sessionId) {
        this.x = startX;
        this.y = startY;
        this.role = role;
        this.out = out;
        this.sessionId = sessionId;
        this.mapIdx = 5;
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
        //System.out.println("("+x+","+y+")");
    }

    public void sendInteraction(){
        String message = String.format("202|%s|%d|%d|%d|1", sessionId, x, y, mapIdx);
        out.println(message);
        out.flush();
    }

    private void sendLocation() {
        String message = String.format("201|%s|%d|%d|%d|1", sessionId, x, y, mapIdx);
        out.println(message);
        out.flush();
    }
}


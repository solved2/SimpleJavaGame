package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Objects;

public class Player {
    private int x, y;
    private Image image;
    private String role;
    private int id;
    private int roomNumber;
    private PrintWriter out;
    private static final int IMAGE_SIZE_X = 30;
    private static final int IMAGE_SIZE_Y = 50;

    public Player(int id, int startX, int startY, String role, String imagePath, PrintWriter out) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.role = role;
        this.image = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))).getImage();
        this.out = out;
        this.roomNumber = 5; // 초기 roomNumber 설정 (중앙 맵)
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getImageSizeX() {
        return IMAGE_SIZE_X;
    }

    public int getImageSizeY() {
        return IMAGE_SIZE_Y;
    }

    //todo: 술래, 도둑 랜덤 설정 필요
    public String getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        sendLocation();
    }

    private void sendLocation() {
        String message = String.format("1|%d|%d|%d|%d", id, x, y, roomNumber);
        out.println(message);
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, IMAGE_SIZE_X, IMAGE_SIZE_Y, null);
    }
}


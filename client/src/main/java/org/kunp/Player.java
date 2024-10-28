package org.kunp;

import javax.swing.*;
import java.awt.*;

public class Player {
    private int x, y;
    private Image image;
    private String role;
    private static final int IMAGE_SIZE_X = 28;
    private static final int IMAGE_SIZE_Y = 53;// 이미지 크기를 작게 설정

    public Player(int startX, int startY, String role, String imagePath) {
        this.x = startX;
        this.y = startY;
        this.role = role;
        this.image = new ImageIcon(getClass().getResource(imagePath)).getImage();
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

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, IMAGE_SIZE_X, IMAGE_SIZE_Y, null); // 이미지를 작게 그리기
    }
}



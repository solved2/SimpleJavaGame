package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Map extends JFrame {
    private static final int VIEW_SIZE = 500;
    private static final int MOVE_STEP = 25;
    private MapPanel[][] maps;
    private int currentMapX = 1, currentMapY = 1;
    private List<Player> players;

    public Map() {
        setTitle("Tag Game");
        setSize(VIEW_SIZE, VIEW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        players = new ArrayList<>();
        players.add(new Player(VIEW_SIZE / 2, VIEW_SIZE / 2, "술래", "/tagger.png")); // 술래 이미지 경로 설정
        players.add(new Player(VIEW_SIZE / 2 + 50, VIEW_SIZE / 2 + 50, "도둑", "/normal.png")); // 도둑 이미지 경로 설정

        maps = new MapPanel[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                maps[i][j] = new MapPanel(i, j, players);
            }
        }
        setLayout(new BorderLayout());
        add(maps[currentMapX][currentMapY], BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
            }
        });
        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);
    }

    private void movePlayer(int keyCode) {
        Player player = players.get(0); // 현재는 첫 번째 플레이어만 이동
        int newX = player.getX(), newY = player.getY();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                newY = Math.max(player.getY() - MOVE_STEP, 0);
                break;
            case KeyEvent.VK_DOWN:
                newY = Math.min(player.getY() + MOVE_STEP, VIEW_SIZE - MOVE_STEP);
                break;
            case KeyEvent.VK_LEFT:
                newX = Math.max(player.getX() - MOVE_STEP, 0);
                break;
            case KeyEvent.VK_RIGHT:
                newX = Math.min(player.getX() + MOVE_STEP, VIEW_SIZE - MOVE_STEP);
                break;
        }
        if (maps[currentMapX][currentMapY].isPortal(newX, newY)) {
            moveMap(newX, newY);
        } else {
            player.move(newX - player.getX(), newY - player.getY());
            repaint();
        }
        updateLocationLabel(); // 위치 업데이트
    }

    private void moveMap(int x, int y) {
        int newX = currentMapX, newY = currentMapY;
        if (x == 0) newX = Math.max(currentMapX - 1, 0); // 서쪽 포탈
        if (x == VIEW_SIZE - MOVE_STEP) newX = Math.min(currentMapX + 1, 2); // 동쪽 포탈
        if (y == 0) newY = Math.max(currentMapY - 1, 0); // 북쪽 포탈
        if (y == VIEW_SIZE - MOVE_STEP) newY = Math.min(currentMapY + 1, 2); // 남쪽 포탈

        remove(maps[currentMapX][currentMapY]);
        currentMapX = newX;
        currentMapY = newY;
        for (Player player : players) {
            player.move(VIEW_SIZE / 2 - player.getX(), VIEW_SIZE / 2 - player.getY());
        }
        add(maps[currentMapX][currentMapY], BorderLayout.CENTER);
        revalidate();
        repaint();
        requestFocusInWindow(); // 포커스를 다시 설정
        updateLocationLabel(); // 위치 업데이트
    }

    private void updateLocationLabel() {
        Player player = players.get(0); // 현재는 첫 번째 플레이어만 출력
        System.out.println("현재 맵: (" + currentMapX + ", " + currentMapY + ") | 플레이어 위치: (" + player.getX() + ", " + player.getY() + ")");
    }
}


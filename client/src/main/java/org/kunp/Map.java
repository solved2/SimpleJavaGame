package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

public class Map extends JPanel {
    private static final int VIEW_SIZE = 500;
    private static final int MOVE_STEP = 10;
    private MapPanel[][] maps;
    private int currentMapX = 1, currentMapY = 1;
    private final Player player;
    private final boolean[] keysPressed = new boolean[256]; // 키 상태 추적용 배열
    private final Timer moveTimer;
    private final PrintWriter out;
    private final String sessionId;

    public Map(PrintWriter out, Player player, String sessionId) {
        this.out = out;
        this.player = player;
        this.sessionId = sessionId;
        setPreferredSize(new Dimension(VIEW_SIZE, VIEW_SIZE));
        setFocusable(true);
        initializeMapPanels();

        /* 방향키를 눌렀을 때 이동에 지연어 없도록 하기위함 */
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed[e.getKeyCode()] = true; // 키가 눌렸음을 저장
            }
            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed[e.getKeyCode()] = false; // 키가 떼어졌음을 저장
            }
        });

        // Timer 설정: 50ms 마다 유저의 키 상태를 체크
        moveTimer = new Timer(50, e -> checkMovement());
        moveTimer.start();

        requestFocusInWindow();
    }

    private void initializeMapPanels() {
        maps = new MapPanel[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                maps[i][j] = new MapPanel(i, j, player);
            }
        }
        setLayout(new BorderLayout());
        add(maps[currentMapX][currentMapY], BorderLayout.CENTER);
    }

    private void checkMovement() {
        if (keysPressed[KeyEvent.VK_UP]) movePlayer(KeyEvent.VK_UP);
        if (keysPressed[KeyEvent.VK_DOWN]) movePlayer(KeyEvent.VK_DOWN);
        if (keysPressed[KeyEvent.VK_LEFT]) movePlayer(KeyEvent.VK_LEFT);
        if (keysPressed[KeyEvent.VK_RIGHT]) movePlayer(KeyEvent.VK_RIGHT);
    }

    private void movePlayer(int keyCode) {
        int newX = player.getX(), newY = player.getY();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                newY = Math.max(player.getY() - MOVE_STEP, 0);
                break;
            case KeyEvent.VK_DOWN:
                newY = Math.min(player.getY() + MOVE_STEP, VIEW_SIZE - player.getImageSizeY());
                break;
            case KeyEvent.VK_LEFT:
                newX = Math.max(player.getX() - MOVE_STEP, 0);
                break;
            case KeyEvent.VK_RIGHT:
                newX = Math.min(player.getX() + MOVE_STEP, VIEW_SIZE - player.getImageSizeX());
                break;
        }

        //portal 이동 감지 후 서버에게 room number 변경해서 보냄
        int portal;
        if ((portal = maps[currentMapX][currentMapY].isPortal(newX, newY)) != -1) {
            moveMap(newX, newY, portal);
        } else {
            player.move(newX - player.getX(), newY - player.getY());
            repaint();
        }
        updateLocationLabel();
    }

    private void moveMap(int x, int y, int portal) {
        int newX = currentMapX, newY = currentMapY;
        if (portal == 2) newX = Math.max(currentMapX - 1, 0); // 서쪽 포탈
        if (portal == 3) newX = Math.min(currentMapX + 1, 2); // 동쪽 포탈
        if (portal == 0) newY = Math.max(currentMapY - 1, 0); // 북쪽 포탈
        if (portal == 1) newY = Math.min(currentMapY + 1, 2); // 남쪽 포탈

        remove(maps[currentMapX][currentMapY]);
        currentMapX = newX;
        currentMapY = newY;
        player.move(VIEW_SIZE / 2 - player.getX(), VIEW_SIZE / 2 - player.getY());
        player.setRoomNumber(newY * 3 + newX + 1);
        add(maps[currentMapX][currentMapY], BorderLayout.CENTER);
        revalidate();
        repaint();
        requestFocusInWindow();
        updateLocationLabel();
    }

    private void updateLocationLabel() {
        System.out.println("현재 맵: (" + currentMapX + ", " + currentMapY + ") | 플레이어 위치: (" + player.getX() + ", " + player.getY() + ") | Room Number: " + player.getRoomNumber());
    }
}

package org.kunp.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

public class Map extends JPanel {
    private MapPanel[][] maps;
    private int currentMapX = 1, currentMapY = 1;
    private final Player player;
    private final boolean[] keysPressed = new boolean[256];
    private final Timer moveTimer;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String sessionId;
    private final HashMap<String, Location> locations = new HashMap<>();

    public Map(BufferedReader in, PrintWriter out, Player player, String sessionId) {
        this.in = in;
        this.out = out;
        this.player = player;
        this.sessionId = sessionId;

        setPreferredSize(new Dimension(Constants.MAP_SIZE, Constants.MAP_SIZE));
        setFocusable(true);
        initializeMapPanels();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed[e.getKeyCode()] = true;
            }
            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed[e.getKeyCode()] = false;
            }
        });

        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                for (int i = 0; i < keysPressed.length; i++) {
                    keysPressed[i] = false;
                }
            }
            @Override
            public void focusGained(FocusEvent e) {
            }
        });

        moveTimer = new Timer(50, e -> checkKeyboardInput());
        moveTimer.start();
        requestFocusInWindow();

        LocationSyncThread lst = new LocationSyncThread(in);
        lst.start();
    }

    private void initializeMapPanels() {
        maps = new MapPanel[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                maps[i][j] = new MapPanel(i, j, locations);
            }
        }

        player.move(Constants.MAP_SIZE / 2 - player.getX(), Constants.MAP_SIZE / 2 - player.getY());
        setLayout(new BorderLayout());
        add(maps[currentMapX][currentMapY], BorderLayout.CENTER);
    }

    private void checkKeyboardInput() {
        if (keysPressed[KeyEvent.VK_UP]) movePlayer(KeyEvent.VK_UP);
        if (keysPressed[KeyEvent.VK_DOWN]) movePlayer(KeyEvent.VK_DOWN);
        if (keysPressed[KeyEvent.VK_LEFT]) movePlayer(KeyEvent.VK_LEFT);
        if (keysPressed[KeyEvent.VK_RIGHT]) movePlayer(KeyEvent.VK_RIGHT);

        if (keysPressed[KeyEvent.VK_E] || keysPressed[KeyEvent.VK_SPACE]) {
            player.sendInteraction();
        }
    }

    private void movePlayer(int keyCode) {
        int newX = player.getX(), newY = player.getY();
        int xx = 0, yy = 0;
        switch (keyCode) {
            case KeyEvent.VK_UP:
                yy = Math.max(player.getY() - Constants.CELL_SIZE, 0);
                if(maps[currentMapX][currentMapY].movable(newX, yy)) newY = yy;
                break;
            case KeyEvent.VK_DOWN:
                yy = Math.min(player.getY() + Constants.CELL_SIZE, Constants.MAP_SIZE - Constants.PLAYER_SIZE_Y);
                if(maps[currentMapX][currentMapY].movable(newX, yy)) newY = yy;
                break;
            case KeyEvent.VK_LEFT:
                xx = Math.max(player.getX() - Constants.CELL_SIZE, 0);
                if(maps[currentMapX][currentMapY].movable(xx, newY)) newX = xx;
                break;
            case KeyEvent.VK_RIGHT:
                xx = Math.min(player.getX() + Constants.CELL_SIZE, Constants.MAP_SIZE - Constants.PLAYER_SIZE_X);
                if(maps[currentMapX][currentMapY].movable(xx, newY)) newX = xx;
                break;
        }

        int portal;
        if ((portal = maps[currentMapX][currentMapY].isPortal(newX, newY)) != -1) {
            moveMap(newX, newY, portal);
        } else {
            player.move(newX - player.getX(), newY - player.getY());
        }
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
        player.setMapIdx(newY * 3 + newX + 1);

        int portalSize = Constants.CELL_SIZE * 5;
        if (portal == 2) player.move(Constants.MAP_SIZE - 2*portalSize - Constants.PLAYER_SIZE_X - 10, 0);
        if (portal == 3) player.move(-Constants.MAP_SIZE + 2*portalSize + 10, 0);
        if (portal == 0) player.move(0, Constants.MAP_SIZE - 2*portalSize - Constants.PLAYER_SIZE_Y-20);
        if (portal == 1) player.move(0, -Constants.MAP_SIZE + 2*portalSize + Constants.PLAYER_SIZE_Y+20);
        add(maps[currentMapX][currentMapY], BorderLayout.CENTER);
        revalidate();
        requestFocusInWindow();
    }

    private class LocationSyncThread extends Thread{
        private BufferedReader in = null;
        public LocationSyncThread(BufferedReader in){
            this.in = in;
        }
        public void run(){
            try {
                String line = null;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    String[] parts = line.split("\\|");
                    String sessionId = parts[1];
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    int roomNumber = Integer.parseInt(parts[4]);
                    synchronized (locations){
                        if(!locations.containsKey(sessionId)) locations.put(sessionId, new Location(roomNumber, x, y));
                        else locations.get(sessionId).setLocation(roomNumber, x, y);
                    }
                    repaint();
                }
            }catch(Exception ex){
            }finally{
                try{
                    if(in != null) in.close();
                }catch(Exception ex){}
            }
        }
    }
}

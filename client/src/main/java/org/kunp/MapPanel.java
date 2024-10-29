package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MapPanel extends JPanel {
    private static final int MAP_SIZE = 500;
    private static final int CELL_SIZE = 25;
    private static final int OFFSET = 10;
    private int mapX, mapY;
    private Rectangle[] portals;
    private Image portalImage;
    private List<Player> players;

    public MapPanel(int mapX, int mapY, List<Player> players) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.players = players;
        setPreferredSize(new Dimension(MAP_SIZE, MAP_SIZE));
        setFocusable(true);
        initializePortals();
        portalImage = new ImageIcon(getClass().getResource("/portal.png")).getImage();
    }

    private void initializePortals() {
        portals = new Rectangle[4];
        int portalSize = CELL_SIZE * 2;
        portals[0] = new Rectangle(MAP_SIZE / 2 - portalSize / 2, 0, portalSize, portalSize); // 북쪽 포탈
        portals[1] = new Rectangle(MAP_SIZE / 2 - portalSize / 2, MAP_SIZE - portalSize - OFFSET, portalSize, portalSize); // 남쪽 포탈
        portals[2] = new Rectangle(0, MAP_SIZE / 2 - portalSize / 2, portalSize, portalSize); // 서쪽 포탈
        portals[3] = new Rectangle(MAP_SIZE - portalSize, MAP_SIZE / 2 - portalSize / 2, portalSize, portalSize); // 동쪽 포탈

        // 가장자리 맵의 경우 접근 가능한 쪽에만 포탈 배치
        if (mapX == 0) portals[2] = null; // 서쪽 포탈 제거
        if (mapX == 2) portals[3] = null; // 동쪽 포탈 제거
        if (mapY == 0) portals[0] = null; // 북쪽 포탈 제거
        if (mapY == 2) portals[1] = null; // 남쪽 포탈 제거
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        drawPlayers(g);
    }

    private void drawMap(Graphics g) {
        g.setColor(Color.GRAY);
        for (int i = 0; i < MAP_SIZE; i += CELL_SIZE) {
            for (int j = 0; j < MAP_SIZE; j += CELL_SIZE) {
                g.drawRect(i, j, CELL_SIZE, CELL_SIZE);
            }
        }
        // 포탈 그리기
        for (Rectangle portal : portals) {
            if (portal != null) {
                g.drawImage(portalImage, portal.x, portal.y, portal.width, portal.height, null);
            }
        }
    }

    private void drawPlayers(Graphics g) {
        for (Player player : players) {
            player.draw(g);
        }
    }

    public boolean isPortal(int x, int y) {
        for (Rectangle portal : portals) {
            if (portal != null && portal.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
}



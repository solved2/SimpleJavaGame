package org.kunp.map;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MapPanel extends JPanel {
    private int mapX, mapY;
    private Rectangle[] portals;

    private int[][] state = new int[Constants.MAP_SIZE][Constants.MAP_SIZE];
    private HashMap<String, Location> locations = null;

    public MapPanel(int mapX, int mapY, HashMap<String, Location> locations) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.locations = locations;

        initializePortals();
        setObstacles();
        if(mapX == 0 && mapY == 0) setFence();
        setPreferredSize(new Dimension(Constants.MAP_SIZE, Constants.MAP_SIZE));
        setFocusable(true);
    }

    private void setObstacles() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 20; i <= 470; i += 10) numbers.add(i);

        Random random = new Random(3*mapX+mapY);
        List<int[]> locs = new ArrayList<>();
        while (locs.size() < Constants.NUM_ROCKS) {
            int x = numbers.get(random.nextInt(numbers.size()));
            int y = numbers.get(random.nextInt(numbers.size()));
            if (!canSetObstacle(x, y)) continue;
            boolean conflict = locs.stream().noneMatch(loc ->
                    Math.abs(x - loc[0]) <= 60 && Math.abs(y - loc[1]) <= 60
            );
            if (!conflict) continue;
            locs.add(new int[]{x, y});
        }

        for (int[] loc : locs) {
            int rx = loc[0], ry = loc[1];
            state[rx][ry] = Constants.ROCK_START;
            for(int r=rx-Constants.PLAYER_SIZE_X+15; r<=rx+Constants.ROCK_SIZE_X-10; r++){
                for(int c=ry-Constants.PLAYER_SIZE_Y+15; c<=ry+Constants.ROCK_SIZE_Y-Constants.PLAYER_SIZE_Y; c++){
                    if(rx == r && ry == c) continue;
                    if(r < 0 || r >= Constants.MAP_SIZE || c < 0 || c >= Constants.MAP_SIZE) continue;
                    state[r][c] = Constants.ROCK;
                }
            }
        }
    }

    private void setFence(){
        for(int r=0; r<Constants.MAP_SIZE; r++){
            for(int c=20; c<=50; c++) {
                state[r][c] = Constants.ROCK;
            }
        }
    }

    private void initializePortals() {
        portals = new Rectangle[4];
        int portalSize = Constants.CELL_SIZE * 5;
        portals[0] = new Rectangle(Constants.MAP_SIZE / 2 - portalSize / 2, 0, portalSize, portalSize); // 북쪽 포탈
        portals[1] = new Rectangle(Constants.MAP_SIZE / 2 - portalSize / 2, Constants.MAP_SIZE - portalSize, portalSize, portalSize); // 남쪽 포탈
        portals[2] = new Rectangle(0, Constants.MAP_SIZE / 2 - portalSize / 2, portalSize, portalSize); // 서쪽 포탈
        portals[3] = new Rectangle(Constants.MAP_SIZE - portalSize, Constants.MAP_SIZE / 2 - portalSize / 2, portalSize, portalSize); // 동쪽 포탈
        if (mapX == 0) portals[2] = null;
        if (mapX == 2) portals[3] = null;
        if (mapY == 0) portals[0] = null;
        if (mapY == 2) portals[1] = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        drawPlayers(g);
    }

    private void drawMap(Graphics g) {
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < Constants.MAP_SIZE; i += Constants.CELL_SIZE) {
            for (int j = 0; j < Constants.MAP_SIZE; j += Constants.CELL_SIZE) {
                g.drawRect(i, j, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }

        for (int i = 0; i < Constants.MAP_SIZE; i += Constants.CELL_SIZE) {
            for (int j = 0; j < Constants.MAP_SIZE; j += Constants.CELL_SIZE) {
                if(state[i][j] == Constants.ROCK_START) g.drawImage(Constants.rockImage, i, j, Constants.ROCK_SIZE_X, Constants.ROCK_SIZE_Y, null);
            }
        }

        for (Rectangle portal : portals) {
            if (portal != null) {
                g.drawImage(Constants.portalImage, portal.x, portal.y, portal.width, portal.height, null);
            }
        }

        if(this.mapX == 0 && this.mapY == 0){
            for (int i = 0; i <= Constants.MAP_SIZE; i += 3*Constants.CELL_SIZE) {
                g.drawImage(Constants.fenceImage, i, 50, 30, 30, null);
            }
            g.drawImage(Constants.buttonImage, 70, 52, 25, 25, null);
        }
    }

    private void drawPlayers(Graphics g) {
        synchronized (locations) {
            Collection collection = locations.values();
            Iterator iter = collection.iterator();
            while(iter.hasNext()){
                Location loc = (Location) iter.next();
                if(loc.getRoomNumber() == mapY * 3 + mapX + 1){
                    if(loc.getRole() == 0) g.drawImage(Constants.taggerImage, loc.getX(), loc.getY(), Constants.PLAYER_SIZE_X, Constants.PLAYER_SIZE_Y, null);
                    else g.drawImage(Constants.runawayImage, loc.getX(), loc.getY(), Constants.PLAYER_SIZE_X, Constants.PLAYER_SIZE_Y, null);
                }
            }
        }
    }

    public int isPortal(int x, int y) {
        Rectangle portalRange = new Rectangle(x, y, 1, 60);
        for (int i = 0; i < portals.length; i++) {
            if (portals[i] != null && portals[i].intersects(portalRange)) {
                return i;
            }
        }
        return -1;
    }

    public boolean canSetObstacle(int x, int y) {
        Rectangle obstacle_area = new Rectangle(x-50, y-50, 150, 150);
        Rectangle fence_area = new Rectangle(0, 0, Constants.MAP_SIZE, 100);
        for (int i = 0; i < portals.length; i++) {
            if (portals[i] != null && portals[i].intersects(obstacle_area)) return false;
        }
        if(this.mapX == 0 && this.mapY == 0){
            if (fence_area.intersects(obstacle_area)) return false;
        }
        return true;
    }

    public boolean movable(int x, int y) {
        if(state[x][y] == Constants.ROCK || state[x][y] == Constants.ROCK_START) return false;
        return true;
    }
}

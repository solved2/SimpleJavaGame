package org.kunp.Servlet.message;

import java.io.Serializable;

public class GameMessage extends Message implements Serializable {
    private int x;
    private int y;
    private int roomNumber;
    private int gameId;

  public GameMessage(int type, String id, int x, int y, int roomNumber, int gameId) {
    super(type, id);
    this.x = x;
    this.y = y;
    this.roomNumber = roomNumber;
    this.gameId = gameId;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  public int getGameId() {
    return gameId;
  }

  public static GameMessage of(String[] tokens) {
    return new GameMessage(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
  }
}

package org.kunp.Servlet.message;

import java.io.Serializable;

public class MenuMessage extends Message implements Serializable {

  private final String roomName;
  private int roomNumber;
  private int gameId;

  public MenuMessage(int type, String id, String roomName, int roomNumber, int gameId) {
    super(type, id);
    this.roomName = roomName;
    this.roomNumber = roomNumber;
    this.gameId = gameId;
  }

  public static MenuMessage of(String[] tokens) {
    return new MenuMessage(Integer.parseInt(tokens[0]), tokens[1], tokens[2], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
  }

  public String getRoomName() {
    return roomName;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  public int getGameId() {
    return gameId;
  }
}

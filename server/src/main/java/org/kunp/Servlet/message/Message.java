package org.kunp.Servlet.message;

import java.io.Serializable;

public class Message implements Serializable {
  private final int type; // 1. 움직임, 2. 상호작용
  private final String id;
  private final int x;
  private int y;
  private int roomNumber;
  private int gameId;

  public Message(int type, String id, int x, int y, int roomNumber, int gameId) {
    this.type = type;
    this.id = id;
    this.x = x;
    this.y = y;
    this.roomNumber = roomNumber;
    this.gameId = gameId;
  }

  /***
   * parsing String message to Message obj
   * @param message String
   * @return Message
   */
  public static Message parse(String message) {
    String[] tokens = message.split("\\|");
    return new Message(
        Integer.parseInt(tokens[0]),
        tokens[1],
        Integer.parseInt(tokens[2]),
        Integer.parseInt(tokens[3]),
        Integer.parseInt(tokens[4]),
        Integer.parseInt(tokens[5]));
  }

  public int getType() {
    return type;
  }

  public String getId() {
    return id;
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

  @Override
  public String toString() {
    return String.format("%d|%s|%d|%d|%d|%d", type, id, x, y, roomNumber, gameId);
  }
}

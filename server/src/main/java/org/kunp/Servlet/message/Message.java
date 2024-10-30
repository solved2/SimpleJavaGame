package org.kunp.Servlet.message;

import java.io.Serializable;

public class Message implements Serializable {
  private final int type; // 1. 움직임, 2. 상호작용
  private final int id;
  private final int x;
  private int y;
  private int roomNumber;

  public Message(int type, int id, int x, int y, int roomNumber) {
    this.type = type;
    this.id = id;
    this.x = x;
    this.y = y;
    this.roomNumber = roomNumber;
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
        Integer.parseInt(tokens[1]),
        Integer.parseInt(tokens[2]),
        Integer.parseInt(tokens[3]),
        Integer.parseInt(tokens[4]));
  }

  public int getType() {
    return type;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  public void setRoomNumber(int roomNumber) {
    this.roomNumber = roomNumber;
  }

  @Override
  public String toString() {
    return String.format("%d|%d|%d|%d|%d", type, id, x, y, roomNumber);
  }
}

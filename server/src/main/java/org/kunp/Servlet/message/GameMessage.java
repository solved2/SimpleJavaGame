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
    if (tokens.length < 6) {
      throw new IllegalArgumentException("Invalid tokens length. Expected 6 tokens.");
    }
    return new GameMessage(
            Integer.parseInt(tokens[0]),   // type
            tokens[1],                    // id
            Integer.parseInt(tokens[2]),  // x
            Integer.parseInt(tokens[3]),  // y
            Integer.parseInt(tokens[4]),  // roomNumber
            Integer.parseInt(tokens[5])   // gameId
    );
  }
}

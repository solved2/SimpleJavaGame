package org.kunp.Servlet.message;

import java.io.Serializable;

public class MenuMessage extends Message implements Serializable {

  private final String roomName;
  private final int userLimit;
  private final int timeLimit;
  private int roomNumber;
  private int gameId;

  public MenuMessage(int type, String sessionId, String roomName, int userLimit, int timeLimit) {
    super(type, sessionId);

    // 방 이름에 "," 허용하지 않도록 검증
    if (roomName.contains(",")) {
      throw new IllegalArgumentException("Room name cannot contain ','");
    }
    this.roomName = roomName;
    this.userLimit = userLimit;
    this.timeLimit = timeLimit;
  }

  // Factory 메서드: 문자열 배열로부터 객체 생성
  public static MenuMessage of(String[] tokens) {
    if (tokens.length < 5) {
      throw new IllegalArgumentException("Invalid number of tokens for MenuMessage");
    }
    return new MenuMessage(
            Integer.parseInt(tokens[0]), // type
            tokens[1],                  // sessionId
            tokens[2],                  // roomName
            Integer.parseInt(tokens[3]), // userLimit
            Integer.parseInt(tokens[4]) // timeLimit
    );
  }

  // Getter 메서드
  public String getRoomName() {
    return roomName;
  }

  public int getUserLimit() {
    return userLimit;
  }

  public int getTimeLimit() {
    return timeLimit;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  public void setRoomNumber(int roomNumber) {
    this.roomNumber = roomNumber;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }
}

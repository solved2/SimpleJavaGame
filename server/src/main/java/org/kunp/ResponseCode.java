package org.kunp;

public enum ResponseCode {
  WAITROOM_ENTER(110),
  WAITROOM_LEAVE(111),
  WAITROOM_LIST(112),
  GAME_START(113),
  GAME_PLAYER_MOVE(210),
  GAME_PLAYER_CATCH(211),
  GAME_PLAYER_ESCAPE(212),
  GAME_END(213),
  GAME_PLAYER_LEAVE(214),;

  private final int code;

  ResponseCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  @Override
  public String toString() {
    return String.valueOf(code);
  }
}

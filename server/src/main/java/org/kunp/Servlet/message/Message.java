package org.kunp.Servlet.message;

public abstract class Message {
  private final int type;
  private final String id;

  public Message(int type, String id) {
    this.type = type;
    this.id = id;
  }

  public int getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public boolean isGameMessage() {
    return this.type >= 200;
  }

  public boolean isMenuMessage() {
    return this.type < 200;
  }
}

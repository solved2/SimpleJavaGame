package org.kunp.Servlet.menu;

import org.kunp.Servlet.session.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WaitingRoomContext {
  private final Map<String, OutputStream> participants = new ConcurrentHashMap<>();

  private final String roomName;
  private int roomNumber;
  private int gameId;

  public WaitingRoomContext(String roomName) {
    this.roomName = roomName;
  }

  //110번 : 입장 메세지
  public synchronized void enter(Session session) {
    if(participants.containsKey(session.getSessionId())) return;
    participants.put(session.getSessionId(), (OutputStream) session.getAttributes().get("ops"));
    for (Map.Entry<String, OutputStream> entry : participants.entrySet()) {
      broadCast("110|%s|%s|%s|%s\n".formatted(entry.getKey(), "name", "content", "time"));
    }
  }

  //111번 : 퇴장 메세지
  public synchronized void leave(Session session) {
    participants.remove(session.getSessionId());
    broadCast("111|%s|%s|%s|%s\n".formatted(session.getSessionId(), "name", "content", "time"));
  }

  public void broadCast(String message) {
    for (OutputStream oos : participants.values()) {
      try {
          oos.write(message.getBytes());
          oos.flush();
      } catch (SocketException e) {
        //participants.remove(oos);
      } catch (IOException e) {
        //throw new RuntimeException(e);
      }
    }
  }

  public String getRoomName() {
    return roomName;
  }
}

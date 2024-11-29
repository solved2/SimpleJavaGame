package org.kunp.Servlet.menu;

import org.kunp.Servlet.game.GameContextRegistry;
import org.kunp.Servlet.game.GameRequestHandler;
import org.kunp.Servlet.session.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.kunp.Servlet.menu.WaitingRoomRegistry.getInstance;

public class WaitingRoomContext {
  private final Map<String, OutputStream> participants = new ConcurrentHashMap<>();

  private final String roomName;
  private String hostId;
  private int roomNumber;
  private int gameId;

  public WaitingRoomContext(String roomName, String hostId) {
    this.roomName = roomName;
    this.hostId = hostId;
  }

  //110번 : 입장 메세지
  public synchronized void enter(Session session) {
    if(participants.containsKey(session.getSessionId())) return;
    participants.put(session.getSessionId(), (OutputStream) session.getAttributes().get("ops"));
    for (Map.Entry<String, OutputStream> entry : participants.entrySet()) {
      broadCast("110|%s\n".formatted(entry.getKey()));
    }
  }

  //111번 : 퇴장 메세지
  public synchronized void leave(Session session) {
    System.out.println("leave");
    broadCast("111|%s\n".formatted(session.getSessionId()));
    participants.remove(session.getSessionId());
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

  public boolean isHost(String sessionId) {
    return hostId.equals(sessionId);
  }
  public String getRoomName() {
    return roomName;
  }

  public void initGame(Session session) {
    GameRequestHandler.getInstance().createGameContextAndJoinAll(roomName, session, participants);
  }
}

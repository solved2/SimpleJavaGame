package org.kunp.Servlet.menu;

import org.kunp.Servlet.game.GameContextRegistry;
import org.kunp.Servlet.game.GameRequestHandler;
import org.kunp.Servlet.session.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WaitingRoomContext {
  private final Map<String, OutputStream> participants = new ConcurrentHashMap<>();

  private final String roomName;
  private int gameId;
  private String hostId;
  private final int userLimit;
  private final int timeLimit;
  private boolean exposeLevel = true;

  public WaitingRoomContext(String roomName, String hostId, int userLimit, int timeLimit) {
    // 방 이름 검증
    if (roomName.contains(",")) {
      throw new IllegalArgumentException("Room name cannot contain ','");
    }
    this.roomName = roomName;
    this.hostId = hostId;
    this.userLimit = userLimit;
    this.timeLimit = timeLimit;
  }

  // 110번: 입장 메세지
  public synchronized void enter(Session session) throws IOException {

    if (participants.size() >= userLimit) {
      return;
    }

    OutputStream ops = (OutputStream) session.getAttributes().get("ops");
    participants.put(session.getSessionId(), ops);

    // Broadcast to all participants
    for (Map.Entry<String, OutputStream> entry : participants.entrySet()) {
      broadCast("110|%s|%s|content|time\n".formatted(entry.getKey(), roomName));
    }

    // Send updated participant list to the new user
    ops.write("110|%s|%s|content|time\n".formatted(String.join(",", participants.keySet()), roomName).getBytes());
    ops.flush();
  }

  // 111번: 퇴장 메세지
  public void leave(Session session) {
    synchronized (this) {
      participants.remove(session.getSessionId());
      if(participants.isEmpty()) {
        WaitingRoomRegistry.getInstance().removeWaitingRoom(roomName);
        return;
      }
      if(session.getSessionId().equals(hostId)) {
        //select random host
        hostId = participants.keySet().iterator().next();
      }
    }
    broadCast("111|%s\n".formatted(session.getSessionId()));
  }

  public void broadCast(String message) {
    for (OutputStream oos : participants.values()) {
      try {
        oos.write(message.getBytes());
        oos.flush();
      } catch (SocketException e) {
        System.err.println("SocketException: " + e.getMessage());
      } catch (IOException e) {
        System.err.println("IOException: " + e.getMessage());
      }
    }
  }

  public boolean isHost(String sessionId) {
    return hostId.equals(sessionId);
  }

  public boolean isClosed() {
    return exposeLevel;
  }

  public String getRoomName() {
    return roomName;
  }

  public int getUserLimit() {
    return userLimit;
  }

  public int getTimeLimit() {
    return timeLimit;
  }

  public Map<String, OutputStream> getParticipants() {
    return participants;
  }

  // 게임 초기화
  public void initGame(Session session) {
    this.exposeLevel = false;
    this.gameId = GameRequestHandler.getInstance().createGameContextAndJoinAll(this, session);
  }

  public void endGame() {
    this.exposeLevel = true;
    GameContextRegistry.getInstance().endGame(gameId);
    // Broadcast to all participants
    for (Map.Entry<String, OutputStream> entry : participants.entrySet()) {
      broadCast("110|%s|%s|content|time\n".formatted(String.join(",", participants.keySet()), roomName));
    }
  }
}

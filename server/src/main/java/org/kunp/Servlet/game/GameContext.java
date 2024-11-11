package org.kunp.Servlet.game;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kunp.Servlet.session.Session;

//TODO : thread로 따로 빼기
public class GameContext {

  private final Map<String, OutputStream> participants = new ConcurrentHashMap<>();
  private final Map<String, int[]> positions = new ConcurrentHashMap<>();
  private final int gameId;
  private final AtomicBoolean isFinished;

  public GameContext(int gameId, AtomicBoolean isFinished) {
    this.gameId = gameId;
    this.isFinished = isFinished;
  }

  public boolean isFinished() {
    return this.isFinished.get();
  }

  public void updateContext(String sessionId, int x, int y, int roomId) {
    this.positions.putIfAbsent(sessionId, new int[3]);
    this.positions.get(sessionId)[0] = x;
    this.positions.get(sessionId)[1] = y;
    this.positions.get(sessionId)[2] = roomId;
  }

  public void updateAndBroadCast() {
    for (OutputStream oos : participants.values()) {
      try {
        System.out.println(participants.size());
        for(Map.Entry<String, int[]> entry : positions.entrySet()) {
          oos.write(createMessage(1, entry.getValue(), entry.getKey(), this.gameId).getBytes());
          oos.flush();
          System.out.println("send message");
        }
      } catch (SocketException e) {
        //participants.remove(oos);
      } catch (IOException e) {
        //throw new RuntimeException(e);
      }
    }
  }

  public void enter(Session session) {
    if(participants.containsKey(session.getSessionId())) return;
    participants.put(session.getSessionId(), (OutputStream) session.getAttributes().get("ops"));
  }

  public void leave(Session session) {
    participants.remove(session.getSessionId());
  }

  public boolean isEmpty() {
    return participants.isEmpty();
  }

  private String createMessage(int type, int[] position, String id, int gameId) {
    return type + "|" + id + "|" + position[0] + "|" + position[1] + "|" + position[2] + "|" + gameId+
    "\n";
  }
}



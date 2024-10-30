package org.kunp.Servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.kunp.Servlet.message.Message;
import org.kunp.Servlet.session.Session;

//TODO : thread로 따로 빼기
public class GameContext {

  private final Map<String, OutputStream> participants = new ConcurrentHashMap<>();
  private int roomId;

  public void enter(Session session) {
    participants.put(session.getSessionId(), (OutputStream) session.getAttributes().get("ops"));
  }

  public void leave(Session session) {
    participants.remove(session.getSessionId());
  }
  
  public void broadcast(Message message) {
    List<KV> streams = participants.entrySet().stream().map(e -> new KV(e.getKey(), e.getValue())).toList();
    for(KV keyVal : streams) {
      try {
        keyVal.outputStream.write((message + "\n").getBytes(StandardCharsets.UTF_8));
        keyVal.outputStream.flush();
      } catch (SocketException e) {
        participants.remove(keyVal.key);
      } catch (IOException e) {
        //throw new RuntimeException(e);
      }
    }
  }

  private final class KV {
    String key;
    OutputStream outputStream;

    public KV(String key, OutputStream outputStream) {
      this.key = key;
      this.outputStream = outputStream;
    }
  }
}



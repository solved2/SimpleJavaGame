package org.kunp.Servlet;

import org.kunp.Servlet.game.GameContextRegistry;
import org.kunp.Servlet.message.Message;
import org.kunp.Servlet.session.Session;

import java.io.IOException;

public class RequestHandler {

  private static RequestHandler requestHandler;

  private RequestHandler() {
  }

  public static RequestHandler getInstance() {
    if (requestHandler == null) {
      requestHandler = new RequestHandler();
    }
    return requestHandler;
  }

  public void handleRequest(Session session, String originalMessage) throws IOException {
    Message parsedMessage = Message.parse(originalMessage);
    createIfRoomNotExist(session, parsedMessage);
    GameContextRegistry.getInstance().subscribe(session, parsedMessage.getGameId());
    if (parsedMessage.getType() == 1) {
      GameContextRegistry.getInstance().update(parsedMessage);
    } else if (parsedMessage.getType() == 2) {
      System.out.println("interaction");
      GameContextRegistry.getInstance().interact(parsedMessage);
    }
  }

  private void createIfRoomNotExist(Session session, Message message) {
    if(!GameContextRegistry.getInstance().hasGame(message.getGameId())) {
      GameContextRegistry.getInstance().createGameContext("roomName", session.getSessionId());
    }
  }
}

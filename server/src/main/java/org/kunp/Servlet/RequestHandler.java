package org.kunp.Servlet;

import org.kunp.Servlet.game.GameContextRegistry;
import org.kunp.Servlet.message.Message;
import org.kunp.Servlet.session.Session;

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

  public void handleRequest(Session session, String originalMessage) {
    Message parsedMessage = Message.parse(originalMessage);
    createIfRoomNotExist(session, parsedMessage);
    GameContextRegistry.getInstance().subscribe(session, parsedMessage.getGameId());
    if (parsedMessage.getType() == 1) {
      GameContextRegistry.getInstance().update(parsedMessage);
    }
  }

  private void createIfRoomNotExist(Session session, Message message) {
    if(!GameContextRegistry.getInstance().hasGame(message.getGameId())) {
      GameContextRegistry.getInstance().createGameContext("roomName", session.getSessionId());
    }
  }
}

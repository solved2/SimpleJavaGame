package org.kunp.Servlet.game;

import org.kunp.Servlet.message.GameMessage;
import org.kunp.Servlet.session.Session;

import java.io.IOException;

public class GameRequestHandler {
    private static GameRequestHandler gameRequestHandler;

    private GameRequestHandler() {
    }

    public static GameRequestHandler getInstance() {
        if (gameRequestHandler == null) {
            gameRequestHandler = new GameRequestHandler();
        }
        return gameRequestHandler;
    }

    public void handleGameRequest(Session session, GameMessage parsedMessage) throws IOException {
      createIfRoomNotExist(session, parsedMessage);
      GameContextRegistry.getInstance().subscribe(session, parsedMessage.getGameId());
      if (parsedMessage.getType() == 1) {
        GameContextRegistry.getInstance().update(parsedMessage);
      } else if (parsedMessage.getType() == 2) {
        System.out.println("interaction");
        GameContextRegistry.getInstance().interact(parsedMessage);
      }
    }

  private void createIfRoomNotExist(Session session, GameMessage message) {
    if(!GameContextRegistry.getInstance().hasGame(message.getGameId())) {
      GameContextRegistry.getInstance().createGameContext("roomName", session.getSessionId());
    }
  }
}

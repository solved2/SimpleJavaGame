package org.kunp.Servlet.game;

import org.kunp.Servlet.message.GameMessage;
import org.kunp.Servlet.session.Session;
import org.kunp.Servlet.session.SessionManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

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

    public void createGameContextAndJoinAll(String roomName, Session session, Map<String, OutputStream> users, int userlimit, int timelimit) {
        int gameId = GameContextRegistry.getInstance().createGameContext(roomName, session.getSessionId(), userlimit, timelimit);
        for(Map.Entry<String, OutputStream> entry : users.entrySet()) {
            Session user = SessionManager.getInstance().getSession(entry.getKey());
            GameContextRegistry.getInstance().subscribe(user, gameId);
        }
        GameContextRegistry.getInstance().startGameContext(gameId);

    }

    public void handleGameRequest(Session session, GameMessage parsedMessage) throws IOException {
      createIfRoomNotExist(session, parsedMessage, 30, 2); //default 30, 2
      GameContextRegistry.getInstance().subscribe(session, parsedMessage.getGameId());
      System.out.println(parsedMessage.getType());
      if (parsedMessage.getType() == 201) {
        GameContextRegistry.getInstance().update(parsedMessage);
      } else if (parsedMessage.getType() == 202) {
        System.out.println("interaction");
        GameContextRegistry.getInstance().interact(parsedMessage);
      }
    }

  private void createIfRoomNotExist(Session session, GameMessage message, int userlimit, int timelimit) {
    if(!GameContextRegistry.getInstance().hasGame(message.getGameId())) {
      GameContextRegistry.getInstance().createGameContext("roomName", session.getSessionId(), userlimit, timelimit);
    }
  }
}

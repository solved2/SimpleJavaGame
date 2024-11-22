package org.kunp.Servlet;

import org.kunp.Servlet.game.GameContextRegistry;
import org.kunp.Servlet.game.GameRequestHandler;
import org.kunp.Servlet.menu.MenuRequestHandler;
import org.kunp.Servlet.message.GameMessage;
import org.kunp.Servlet.message.MenuMessage;
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
    Message parsedMessage = parseMessage(originalMessage);
    if (parsedMessage.isMenuMessage()) {
      MenuRequestHandler.getInstance().handleMenuRequest(session, (MenuMessage) parsedMessage);
    } else {
      GameRequestHandler.getInstance().handleGameRequest(session, (GameMessage) parsedMessage);
    }
  }

  private void handleMenuRequest(Session session, MenuMessage message) {
    System.out.println("menu");

  }

  private Message parseMessage(String originalMessage) {
    String[] messageTokens = originalMessage.split("\\|");
    // 100번대는 메뉴 메세지, 200번대는 게임 메세지
    if(Integer.parseInt(messageTokens[0]) <200) {
      return MenuMessage.of(messageTokens);
    } else {
      return GameMessage.of(messageTokens);
    }
  }
}

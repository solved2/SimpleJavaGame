package org.kunp.Servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.kunp.Main;
import org.kunp.Servlet.session.Session;
import org.kunp.Servlet.session.SessionManager;

public class NativeConnectionConfigurer extends ConnectionConfigurer {

  public NativeConnectionConfigurer(SessionManager sessionManager) {
    super(sessionManager);
  }

  @Override
  public Runnable configure(Socket socket) throws IOException {
    Session createdSession = sessionManager.createSession();
    InputStream inputStream = socket.getInputStream();
    OutputStream outputStream = socket.getOutputStream();

    Map<String, Object> attr = new HashMap<>();
    attr.put("ops", outputStream);
    createdSession.setAttributes(attr);

    Main.getGameContext().enter(createdSession);
    Runnable clientHandler =
        new ClientHandler(
            inputStream,
            outputStream,
            createdSession,
            sessionId -> sessionManager.removeSession(createdSession.getSessionId()));
    return clientHandler;
  }
}

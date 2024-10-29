package org.kunp.Servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
    Runnable clientHandler =
        new ClientHandler(
            inputStream,
            outputStream,
            createdSession.getSessionId(),
            sessionId -> sessionManager.removeSession(sessionId));
    return clientHandler;
  }
}

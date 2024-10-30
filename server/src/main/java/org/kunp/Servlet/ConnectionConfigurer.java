package org.kunp.Servlet;

import java.io.IOException;
import java.net.Socket;
import org.kunp.Servlet.session.SessionManager;

public abstract class ConnectionConfigurer {
  protected SessionManager sessionManager;

  public ConnectionConfigurer(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  /***
   * Configure the connection
   * @param socket
   * @return sessionId
   */
  public abstract Runnable configure(Socket socket) throws IOException;
}

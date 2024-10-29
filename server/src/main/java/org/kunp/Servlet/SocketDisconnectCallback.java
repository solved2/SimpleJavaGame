package org.kunp.Servlet;

/** Callback for event when Socket connection is closed */
public interface SocketDisconnectCallback {
  void onDisconnect(String sessionId);
}

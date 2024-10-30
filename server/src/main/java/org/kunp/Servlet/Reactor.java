package org.kunp.Servlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reactor class to handle incoming connections and dispatch them to the appropriate handler using a
 * thread pool.
 *
 * <p>This is an initial implementation of the Reactor pattern.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Reactor_pattern">Reactor pattern</a>
 * @see ExecutorService
 */
public class Reactor extends Thread {

  private final ServerSocket serverSocket;
  private final ConnectionConfigurer connectionConfigurer;
  private final ExecutorService threadPool;

  public Reactor(
      ServerSocket serverSocket, ConnectionConfigurer connectionConfigurer, int poolSize) {
    this.serverSocket = serverSocket;
    this.connectionConfigurer = connectionConfigurer;
    this.threadPool = Executors.newFixedThreadPool(poolSize);
  }

  @Override
  public void run() {
    waitForConnection();
  }

  private void waitForConnection() {
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        Runnable clientHandler = connectionConfigurer.configure(socket);
        threadPool.execute(clientHandler);
      } catch (IOException e) {
        throw new RuntimeException("Error occurred while waiting for connection", e);
      }
    }
  }
}

package org.kunp;

import static org.kunp.ServerConstant.*;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.kunp.Servlet.*;
import org.kunp.Servlet.game.GameContextRegistry;
import org.kunp.Servlet.session.*;

public class Main {

  public static final ThreadGroup threadGroup = new ThreadGroup("ActiveThreads");
  private static final List<OutputStream> outputStreams = new CopyOnWriteArrayList<>();
  private static ServerSocket serverSocket;
  private static SessionManager sessionManager;
  private static ConnectionConfigurer connectionConfigurer;

  public static void main(String[] args) {
    initDependencies();
    Thread reactorThread =
        startReactor().orElseThrow(() -> new RuntimeException("Failed to start reactor thread"));
    startMonitorThread();

    try {
      killAllActiveThreads();
      reactorThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      closeServerSocket();
    }
  }

  private static void initDependencies() {
    try {
      serverSocket = new ServerSocket(SERVER_PORT);
      sessionManager = SessionManager.getInstance();
      connectionConfigurer = new NativeConnectionConfigurer(sessionManager);
    } catch (Exception e) {
      System.err.println("Initialization error: " + e.getMessage());
      System.exit(1);
    }
  }

  private static Optional<Thread> startReactor() {
    try {
      Thread connectionThread = new Reactor(serverSocket, connectionConfigurer, THREAD_POOL_SIZE);
      connectionThread.start();
      return Optional.of(connectionThread);
    } catch (Exception e) {
      System.err.println("Error starting reactor thread: " + e.getMessage());
      System.exit(1);
    }
    return Optional.empty();
  }

  private static void startMonitorThread() {
    Thread monitorThread = new Thread(threadGroup, new ThreadMonitor());
    monitorThread.setDaemon(true);
    monitorThread.start();
  }

  private static void killAllActiveThreads() {
    threadGroup.interrupt();
  }

  private static void closeServerSocket() {
    try {
      serverSocket.close();
    } catch (Exception e) {
      System.err.println("Error closing server socket: " + e.getMessage());
    }
  }

  private static class ThreadMonitor implements Runnable {
    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(MONITOR_SLEEP_DURATION_MS);
          System.out.println();
        } catch (InterruptedException e) {
        }
        System.out.println("Active threads: " + Thread.activeCount());
        System.out.println("Active Sessions : " + sessionManager.getSessionCount());
        GameContextRegistry.getInstance().monitor();
      }
    }
  }
}

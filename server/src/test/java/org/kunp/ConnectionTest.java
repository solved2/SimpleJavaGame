package org.kunp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

class ConnectionTest {


  @BeforeAll
  static void startServer() throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "build/classes/java/main", "org.kunp.Main");
    processBuilder.inheritIO();
    processBuilder.start();
    // Wait for the server to start
    try {
      sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  @AfterAll
  static void stopServer() {
    // Kill the server
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("pkill", "-f", "org.kunp.Main");
      processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Accept connections from multiple Clients")
  void testAcceptConnections() throws InterruptedException {
    Runnable client =
        () -> {
          try {
            Socket socket = new Socket(ServerConstant.SERVER_HOST, ServerConstant.SERVER_PORT);
            synchronized (this) {
              wait();
            }
            System.out.println("Client Disconnected");
          } catch (UnknownHostException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          } catch (InterruptedException e) {
            System.out.println("Client Interrupted");
          }
        };

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    for (int i = 0; i < 10; i++) {
      executorService.execute(client);
    }
    sleep(5000);
    synchronized (client) {
      client.notifyAll();
    }
    executorService.shutdown();
  }
}
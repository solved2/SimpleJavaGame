package org.kunp.Servlet;

import java.io.*;
import org.kunp.Main;
import org.kunp.Servlet.message.Message;
import org.kunp.Servlet.session.Session;

/**
 * Handles individual client requests in a separate thread.
 *
 * <p>This class reads input from the client, processes it, and writes the response back to the
 * client. It also handles the disconnection of the client and notifies the callback.
 */
public class ClientHandler implements Runnable {

  private final Session session;
  private final BufferedInputStream inputStream;
  private final BufferedOutputStream outputStream;
  private final SocketDisconnectCallback callback;

  /**
   * Constructs a new ClientHandler.
   *
   * @param ios the input stream from the client
   * @param oos the output stream to the client
   * @param callback the callback to notify when the client disconnects
   */
  public ClientHandler(
      InputStream ios, OutputStream oos, Session session, SocketDisconnectCallback callback) {
    this.session = session;
    this.inputStream = new BufferedInputStream(ios);
    this.outputStream = new BufferedOutputStream(oos);
    this.callback = callback;
  }

  /**
   * Runs the client handler thread.
   *
   * <p>This method reads input from the client, processes it, and writes the response back to the
   * client. It also handles the disconnection of the client and notifies the callback.
   */
  @Override
  public void run() {
    System.out.println("Session ID: " + this.session.getSessionId());
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      processClientRequests(br);
    } catch (IOException e) {
      System.err.println("Client disconnected or an error occurred: " + e.getMessage());
    } finally {
      closeResources();
    }
  }

  /***
   * Main 이라고 생각하면됩니다. Message가 들어오면 어떻게 처리할지 고민해봐요
   */
  private void processClientRequests(BufferedReader br) throws IOException {
    String line;
    while ((line = br.readLine()) != null) {
      System.out.println("Received: " + line);
      Message message = Message.parse(line);
      Main.getGameContext().broadcast(message);
    }
  }

  private void writeToOutputStream(String line) throws IOException {
    outputStream.write((line + "\n").getBytes());
    outputStream.flush();
  }

  private void closeResources() {
    try {
      inputStream.close();
      outputStream.close();
      if (callback != null) {
        callback.onDisconnect(session.getSessionId());
      }
    } catch (IOException e) {
      System.err.println("Error closing streams: " + e.getMessage());
    }
  }
}

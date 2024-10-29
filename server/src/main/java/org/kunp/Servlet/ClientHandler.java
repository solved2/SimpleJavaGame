package org.kunp.Servlet;

import java.io.*;

/**
 * Handles individual client requests in a separate thread.
 *
 * <p>This class reads input from the client, processes it, and writes the response back to the
 * client. It also handles the disconnection of the client and notifies the callback.
 */
public class ClientHandler implements Runnable {

  private final String sessionId;
  private final BufferedInputStream inputStream;
  private final BufferedOutputStream outputStream;
  private final SocketDisconnectCallback callback;

  /**
   * Constructs a new ClientHandler.
   *
   * @param ios the input stream from the client
   * @param oos the output stream to the client
   * @param sessionId the session ID for the client
   * @param callback the callback to notify when the client disconnects
   */
  public ClientHandler(
      InputStream ios, OutputStream oos, String sessionId, SocketDisconnectCallback callback) {
    this.sessionId = sessionId;
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
    System.out.println("Session ID: " + this.sessionId);
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      processClientRequests(br);
    } catch (IOException e) {
      System.err.println("Client disconnected or an error occurred: " + e.getMessage());
    } finally {
      closeResources();
    }
  }

  private void processClientRequests(BufferedReader br) throws IOException {
    String line;
    while ((line = br.readLine()) != null) {
      System.out.println("Received: " + line);
      writeToOutputStream(line);
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
        callback.onDisconnect(sessionId);
      }
    } catch (IOException e) {
      System.err.println("Error closing streams: " + e.getMessage());
    }
  }
}

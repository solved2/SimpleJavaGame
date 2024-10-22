package org.kunp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) throws IOException {
    System.out.println("Hello world!");

    //example codes
    ServerSocket serverSocket = new ServerSocket(8080);
    while (true) {
      Socket accept = serverSocket.accept();

      System.out.println("Accepted connection from " + accept.getInetAddress());
      accept.close();
      break;
    }
    serverSocket.close();
    return;
  }
}

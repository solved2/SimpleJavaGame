package org.kunp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerCommunicator {
    private final BufferedReader in;
    private final PrintWriter out;
    private final List<ServerMessageListener> listeners = new ArrayList<>();

    public interface ServerMessageListener {
        void onMessageReceived(String message);
    }

    public ServerCommunicator(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;

        // 서버 메시지 수신 스레드
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    //System.out.println(message);
                    notifyListeners(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendRequest(String message) {
        out.println(message);
        out.flush();
    }

    public void addMessageListener(ServerMessageListener listener) {
        listeners.add(listener);
    }

    public void removeMessageListener(ServerMessageListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String message) {
        for (ServerMessageListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }
}
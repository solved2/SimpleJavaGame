package org.kunp;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private List<Player> players;

    public Client() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        players = new ArrayList<>();
        players.add(new Player(1, 250, 250, "술래", "/tagger.png", out));
        players.add(new Player(2, 300, 300, "도둑", "/normal.png", out));

        JFrame frame = new JFrame("Game");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Map(out, players)); // Map 객체를 추가
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Client();
    }
}

package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String sessionId;
    private Player player;

    public Client() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sessionId = in.readLine();
            System.out.println("Connected with session ID: " + sessionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        JFrame frame = new JFrame("Tag Game - 대기실");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 500);  // 화면 크기를 키움

        // 대기실 목록 패널
        JPanel parentPanel = new JPanel(new BorderLayout());
        WaitingRoomListPanel waitingRoomListPanel = new WaitingRoomListPanel(in, out, sessionId, parentPanel);
        parentPanel.add(waitingRoomListPanel, BorderLayout.CENTER);

        // 대기실 생성 패널
        WaitingRoomCreationPanel waitingRoomCreationPanel = new WaitingRoomCreationPanel(in, out, sessionId);
        parentPanel.add(waitingRoomCreationPanel, BorderLayout.SOUTH);

        frame.add(parentPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        */
        // 플레이어 생성 (임시 사용자)

        player = new Player(250, 250, "tagger", out, sessionId);

        JFrame frame = new JFrame("Tag Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Map map = new Map(in, out, player, sessionId);
        frame.add(map);
        frame.pack();
        frame.setVisible(true);

    }
}


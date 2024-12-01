package org.kunp.inner;

import org.kunp.ScreenManager;
import org.kunp.ServerCommunicator;
import org.kunp.StateManager;
import org.kunp.map.Map;
import org.kunp.map.Player;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class InnerWaitingRoomControlPanel extends JPanel {

    public InnerWaitingRoomControlPanel(StateManager stateManager, ScreenManager screenManager,ServerCommunicator serverCommunicator, String roomName, BufferedReader in, PrintWriter out) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        setPreferredSize(new Dimension(350, 50));

        JButton startGameButton = new JButton("게임 시작");
        startGameButton.setFocusPainted(false);
        startGameButton.setPreferredSize(new Dimension(100, 30));
        add(startGameButton);

        JButton exitButton = new JButton("나가기");
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(100, 30));
        add(exitButton);

        serverCommunicator.addMessageListener(message -> {
            String[] tokens = message.split("\\|");
            if (tokens[0].equals("113")){
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Game starting...");
                    String role = tokens[1].equals("0") ? "tagger" : "normal";
                    Player player = new Player(
                            Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), role, out, stateManager.getSessionId()
                    );
                    screenManager.addScreen("Map", new Map(in, out, player, stateManager.getSessionId()));
                });
            }
        });

        // 게임 시작 버튼
        startGameButton.addActionListener(e -> {
            String message = String.format("105|%s|%s|%d|%d|1", stateManager.getSessionId(), roomName, 0, 0);
            serverCommunicator.sendRequest(message);
        });

        // 나가기 버튼
        exitButton.addActionListener(e -> {
            String message = String.format("103|%s|%s|%d|%d|1", stateManager.getSessionId(), roomName, 0, 0);
            stateManager.sendServerRequest(message, () -> {
                stateManager.switchTo("WaitingRoom");
            });
        });
    }
}

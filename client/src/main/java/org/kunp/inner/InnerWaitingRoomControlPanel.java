package org.kunp.inner;

import org.kunp.manager.StateManager;

import javax.swing.*;
import java.awt.*;

public class InnerWaitingRoomControlPanel extends JPanel {

    public InnerWaitingRoomControlPanel(StateManager stateManager, String roomName) {
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

        // 게임 시작 버튼
        startGameButton.addActionListener(e -> {
            String message = String.format("105|%s|%s|%d|%d", stateManager.getSessionId(), roomName, 0, 0);
            stateManager.sendServerRequest(message, () -> {});
        });

        // 나가기 버튼
        exitButton.addActionListener(e -> {
            String message = String.format("103|%s|%s|%d|%d", stateManager.getSessionId(), roomName, 0, 0);
            stateManager.sendServerRequest(message, () -> stateManager.switchTo("WaitingRoom"));
        });
    }
}

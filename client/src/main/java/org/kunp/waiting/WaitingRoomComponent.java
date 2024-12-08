package org.kunp.waiting;

import org.kunp.manager.ScreenManager;
import org.kunp.manager.ServerCommunicator;
import org.kunp.manager.StateManager;
import org.kunp.inner.InnerWaitingRoomComponent;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;


public class WaitingRoomComponent extends JPanel {
    public WaitingRoomComponent(String sessionId, String roomName, StateManager stateManager, ScreenManager screenManager, ServerCommunicator serverCommunicator, BufferedReader in, PrintWriter out) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(350, 70));

        JLabel nameLabel = new JLabel(roomName);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(nameLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton enterButton = new JButton("입장");
        enterButton.setFocusPainted(false);
        enterButton.setPreferredSize(new Dimension(60, 30));
        buttonPanel.add(enterButton);

        add(buttonPanel, BorderLayout.SOUTH);

        enterButton.addActionListener(e -> {
            String message = String.format("101|%s|%s|%d|%d", sessionId, roomName, 0, 0);
            // InnerWaitingRoom 화면 (추가된 컴포넌트)
            screenManager.addScreen("InnerWaitingRoom", new InnerWaitingRoomComponent(stateManager, serverCommunicator, screenManager, roomName, in, out));
            stateManager.sendServerRequest(message, () -> {
                stateManager.switchTo("InnerWaitingRoom");
            });
        });
    }
}
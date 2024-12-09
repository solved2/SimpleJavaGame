package org.kunp.waiting;

import org.kunp.manager.ScreenManager;
import org.kunp.manager.ServerCommunicator;
import org.kunp.manager.StateManager;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingRoomListPanel extends JPanel {
    public WaitingRoomListPanel(String sessionId, StateManager stateManager, ServerCommunicator serverCommunicator, ScreenManager screenManager, BufferedReader in, PrintWriter out) {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("대기실 목록"));

        JPanel roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        List<WaitingRoomComponent> rooms = new ArrayList<>();
        JButton refreshButton = new JButton("새로고침");
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 50));

        add(refreshButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        serverCommunicator.addMessageListener(message -> {
            String[] roomData = message.split("\\|");
            SwingUtilities.invokeLater(() -> {
                if(roomData[0].equals("112")) {
                    rooms.clear();
                    roomListPanel.removeAll();
                    if (roomData.length > 1) {
                        Arrays.stream(roomData[1].split(","))
                                .map(el -> new WaitingRoomComponent(sessionId, el, stateManager, screenManager, serverCommunicator, in, out))
                                .forEach(rooms::add);
                    }
                    rooms.forEach(roomListPanel::add);
                    roomListPanel.revalidate();
                    roomListPanel.repaint();
                }
            });
        });

        refreshButton.addActionListener(e -> {
            String requestMessage = String.format("100|%s|%s|%d|%d", sessionId, null, 0, 0); // 요청 메시지 형식
            stateManager.sendServerRequest(requestMessage, () -> {});
        });
    }
}
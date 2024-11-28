package org.kunp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class WaitingRoomListPanel extends JPanel {
    public WaitingRoomListPanel(
            BufferedReader in, PrintWriter out, String sessionId, JPanel parentPanel) {

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("대기실 목록"));

        JPanel roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        Set<String> sessionIds = new HashSet<>();
        List<WaitingRoomComponent> rooms = new ArrayList<>();

        JButton refreshButton = new JButton("새로고침");
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 50));

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.NORTH);

        refreshButton.addActionListener(e -> {
            new Thread(() -> { // 새로운 스레드 생성
                try {
                    out.println("100|" + sessionId + "|null|0|0");
                    out.flush();

                    String message = in.readLine();
                    System.out.println(message);
                    String[] tokens = message.split("\\|");

                    SwingUtilities.invokeLater(() -> { // UI 업데이트를 UI 스레드에서
                        rooms.clear();
                        Arrays.stream(tokens[1].split(","))
                                .map(el -> new WaitingRoomComponent(in, out, sessionId, el, parentPanel, sessionIds))
                                .forEach(rooms::add);

                        roomListPanel.removeAll();
                        rooms.forEach(roomListPanel::add);
                        roomListPanel.revalidate();
                        roomListPanel.repaint();
                    });
                } catch (IOException ex) {
                    ex.printStackTrace(); // 예외 로그
                }
            }).start(); // 스레드 시작
        });
    }
}


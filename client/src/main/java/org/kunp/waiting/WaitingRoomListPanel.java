package org.kunp.waiting;

import org.kunp.server.ServerProtocol;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class WaitingRoomListPanel extends JPanel {
    private final ServerProtocol serverProtocol;

    public WaitingRoomListPanel(
            JPanel parentPanel, BufferedReader in, PrintWriter out, String sessionId) {

        this.serverProtocol = new ServerProtocol(in, out);

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
                    String response = serverProtocol.refreshRoom(sessionId, null, 0, 0);
                    System.out.println(response);
                    String[] tokens = response.split("\\|");

                    if (tokens.length > 1 && !tokens[1].isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            rooms.clear();
                            Arrays.stream(tokens[1].split(","))
                                    .map(el -> new WaitingRoomComponent(in, out, sessionId, el, parentPanel, sessionIds))
                                    .forEach(rooms::add);

                            roomListPanel.removeAll();
                            rooms.forEach(roomListPanel::add);
                            roomListPanel.revalidate();
                            roomListPanel.repaint();
                        });
                    } else {
                        // 대기실이 없는 경우 처리
                        SwingUtilities.invokeLater(() -> {
                            rooms.clear();
                            roomListPanel.removeAll();
                            roomListPanel.revalidate();
                            roomListPanel.repaint();
                            JOptionPane.showMessageDialog(this, "현재 생성된 대기실이 없습니다.", "오류", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // 예외 로그
                }
            }).start(); // 스레드 시작
        });

    }
}


package org.kunp.waiting;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

// 대기실 목록 리스트 패널
public class WaitingRoomListPanel extends JPanel {
    public WaitingRoomListPanel(
            JPanel parentPanel, BufferedReader in, PrintWriter out, String sessionId) {

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("대기실 목록"));

        // 대기실 목록은 스크롤이 가능한 리스트
        JPanel roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        // 마우스 휠 스크롤시 속도 설정
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        List<WaitingRoomComponent> rooms = new ArrayList<>();

        JButton refreshButton = new JButton("새로고침");
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 50));

        add(refreshButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        refreshButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String message = String.format("100|%s|%s|%d|%d|1", sessionId, null, 0, 0);
                    out.println(message);
                    out.flush();
                    String response = in.readLine();
                    String[] tokens = response.split("\\|");

                    SwingUtilities.invokeLater(() -> {
                        try {
                            System.out.println("Running on EDT: " + SwingUtilities.isEventDispatchThread());

                            // Rooms 업데이트
                            rooms.clear();
                            if(tokens.length > 1) {
                                Arrays.stream(tokens[1].split(","))
                                        .map(el -> new WaitingRoomComponent(in, out, sessionId, el, parentPanel))
                                        .forEach(rooms::add);
                            }

                            // RoomListPanel 업데이트
                            roomListPanel.removeAll();
                            rooms.forEach(roomListPanel::add);

                            // 디버깅 로그
                            System.out.println("Number of rooms: " + rooms.size());
                            System.out.println("Room list panel component count: " + roomListPanel.getComponentCount());

                            // 갱신
                            roomListPanel.revalidate();
                            roomListPanel.repaint();

                            // 상위 패널 갱신
                            parentPanel.revalidate();
                            parentPanel.repaint();
                        } catch (Exception exx) {
                            exx.printStackTrace();
                        }
                    });

                } catch (IOException ex) {
                    ex.printStackTrace(); // 예외 로그
                } // 스레드 시작
            }).start();
        });
    }
}

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

// 대기실 목록 리스트 패널
public class WaitingRoomListPanel extends JPanel {
    public WaitingRoomListPanel(
            BufferedReader in, PrintWriter out, String sessionId, JPanel parentPanel) {

        ServerProtocol serverProtocol = new ServerProtocol(in, out);

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("대기실 목록"));

        // 대기실 목록은 스크롤이 가능한 리스트
        JPanel roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        // 마우스 휠 스크롤시 속도 설정
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 임시로 대기실 인원 명단 8인 추가
        Set<String> sessionIds = new HashSet<>();

        // 임시로 대기실 컴포넌트 20개 추가
        //
        List<WaitingRoomComponent> rooms = new ArrayList<>();

        JButton refreshButton = new JButton("새로고침");
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 50));

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.NORTH);

        refreshButton.addActionListener(
                e -> {
                    String message;
                    try {
                        message = serverProtocol.refreshRoom(sessionId, null, 0, 0);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    rooms.clear();
                    String[] tokens = message.split("\\|");
                    Arrays.stream(tokens[1].split(","))
                            .map(
                                    el -> new WaitingRoomComponent(in, out, sessionId, el, parentPanel, sessionIds))
                            .forEach(rooms::add);
                    roomListPanel.removeAll();
                    rooms.stream().forEach(roomListPanel::add);
                    scrollPane.removeAll();
                    scrollPane.add(roomListPanel);
                    scrollPane.revalidate();
                    scrollPane.repaint();
                });
    }
}

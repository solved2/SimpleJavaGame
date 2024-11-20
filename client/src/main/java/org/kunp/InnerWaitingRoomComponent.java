package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

public class InnerWaitingRoomComponent extends JPanel {
    final private String sessionId;
    private List<String> sessionIds;
    private BufferedReader in;
    private PrintWriter out;

    public InnerWaitingRoomComponent(List<String> sessionIds, String roomName, BufferedReader in, PrintWriter out, String sessionId) {
        this.sessionIds = sessionIds;
        this.in = in;
        this.out = out;
        this.sessionId = sessionId;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(350, 200));

        JLabel nameLabel = new JLabel("게임 룸");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(nameLabel, BorderLayout.NORTH);

        // 사용자 목록 패널 추가
        InnerWaitingRoomListPanel listPanel = new InnerWaitingRoomListPanel(sessionIds);
        add(listPanel, BorderLayout.CENTER);

        // 컨트롤 패널 추가
        IntterWaitingRoomControlPanel controlPanel = new IntterWaitingRoomControlPanel(roomName, out, sessionId);
        add(controlPanel, BorderLayout.SOUTH);
    }
}

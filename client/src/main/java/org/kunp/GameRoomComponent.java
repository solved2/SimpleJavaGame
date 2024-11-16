package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

public class GameRoomComponent extends JPanel {
    private List<String> sessionIds;
    private BufferedReader in;
    private PrintWriter out;

    public GameRoomComponent(List<String> sessionIds, BufferedReader in, PrintWriter out) {
        this.sessionIds = sessionIds;
        this.in = in;
        this.out = out;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(350, 200));

        JLabel nameLabel = new JLabel("게임 룸");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(nameLabel, BorderLayout.NORTH);

        // 사용자 목록 패널 추가
        GameRoomListPanel listPanel = new GameRoomListPanel(sessionIds);
        add(listPanel, BorderLayout.CENTER);

        // 컨트롤 패널 추가
        GameRoomControlPanel controlPanel = new GameRoomControlPanel(out);
        add(controlPanel, BorderLayout.SOUTH);
    }
}

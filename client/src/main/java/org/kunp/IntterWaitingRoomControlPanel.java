package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class IntterWaitingRoomControlPanel extends JPanel {
    public IntterWaitingRoomControlPanel(String roomName, PrintWriter out, String sessionId) {
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

        // 게임 시작 버튼 클릭 시 메시지 전송
        startGameButton.addActionListener(e -> {
            //todo : 서버와 입장 메세지 타입 조정 필요
            String message = String.format("0|%s|%s|%d|%d|1", sessionId, roomName, 0, 0);
            out.println(message);
            out.flush();
        });

        // 나가기 버튼 클릭 시 메시지 전송
        exitButton.addActionListener(e -> {
            //todo : 서버와 퇴장 메세지 타입 조정 필요
            String message = String.format("0|%s|%s|%d|%d|1", sessionId, roomName, 0, 0);
            out.println(message);
            out.flush();
        });
    }
}

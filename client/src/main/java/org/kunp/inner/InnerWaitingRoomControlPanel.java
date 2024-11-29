package org.kunp.inner;

import org.kunp.server.ServerProtocol;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class InnerWaitingRoomControlPanel extends JPanel {
    public InnerWaitingRoomControlPanel(String roomName, BufferedReader in, PrintWriter out, String sessionId) {

        ServerProtocol serverProtocol = new ServerProtocol(in, out);
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
            try {
                String message = serverProtocol.startGame(sessionId, roomName, 0, 0);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 나가기 버튼 클릭 시 메시지 전송
        exitButton.addActionListener(e -> {
            //todo : 서버와 퇴장 메세지 타입 조정 필요
            try {
                String message = serverProtocol.exitRoom(sessionId, roomName, 0, 9);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
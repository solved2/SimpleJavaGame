package org.kunp.inner;

import org.kunp.map.Map;
import org.kunp.map.Player;
import org.kunp.waiting.WaitingRoomCreationPanel;
import org.kunp.waiting.WaitingRoomListPanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class InnerWaitingRoomControlPanel extends JPanel {
    public InnerWaitingRoomControlPanel(JPanel parentPanel, String roomName, BufferedReader in, PrintWriter out, String sessionId) {
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

        startGameButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String message = String.format("105|%s|%s|%d|%d|1", sessionId, roomName, 0, 0);
                    out.println(message);
                    out.flush();
                    String response = in.readLine();
                    System.out.println(response);

                    String[] tokens = response.split("\\|");
                    String role;
                    if(Integer.parseInt(tokens[2]) == 0){
                        role = "tagger";
                    }else{
                        role = "normal";
                    }
                    Player player = new Player(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), role, out, sessionId);
                    SwingUtilities.invokeLater(() -> {
                        parentPanel.removeAll();
                        parentPanel.add(new Map(in, out, player, sessionId));
                        parentPanel.revalidate();
                        parentPanel.repaint();
                    });
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();
        });

        exitButton.addActionListener(e -> {
            new Thread(()->{
                String message = String.format("103|%s|%s|%d|%d|1", sessionId, roomName, 0, 0);
                out.println(message);
                out.flush();

                SwingUtilities.invokeLater(() -> {
                    parentPanel.removeAll(); // 기존 컴포넌트 제거

                    // 패널 레이아웃 명확히 설정 (BorderLayout으로 유지)
                    parentPanel.setLayout(new BorderLayout());

                    // 대기실 목록 및 생성 패널 추가
                    parentPanel.add(new WaitingRoomListPanel(parentPanel, in, out, sessionId), BorderLayout.CENTER);
                    parentPanel.add(new WaitingRoomCreationPanel(parentPanel, in, out, sessionId), BorderLayout.SOUTH);

                    // UI 갱신
                    parentPanel.revalidate();
                    parentPanel.repaint();
                });
            }).start();
        });
    }
}
package org.kunp.inner;

import org.kunp.map.Map;
import org.kunp.map.Player;
import org.kunp.server.ServerProtocol;
import org.kunp.waiting.WaitingRoomCreationPanel;
import org.kunp.waiting.WaitingRoomListPanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class InnerWaitingRoomControlPanel extends JPanel {
    private final ServerProtocol serverProtocol;

    public InnerWaitingRoomControlPanel(JPanel parentPanel, String roomName, BufferedReader in, PrintWriter out, String sessionId) {
        this.serverProtocol = new ServerProtocol(in, out);

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
            //todo: 방장인 사람만 게임 시작 가능 -> 서버에서 체크
            new Thread(() -> {
                try {
                    String response = serverProtocol.startGame(sessionId, roomName, 0, 0);
                    System.out.println(response);
                    String[] parts = response.split("\\|");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    String role = parts[3];

                    Player player = new Player(x, y, role, out, sessionId);
                    SwingUtilities.invokeLater(() -> {
                        parentPanel.removeAll();
                        parentPanel.add(new Map(in, out, player, sessionId));
                        parentPanel.revalidate();
                        parentPanel.repaint();
                    });
                } catch (IOException ex) {
                    ex.printStackTrace(); // 예외 로그
                }
            }).start(); // 스레드 시작
        });

        exitButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    serverProtocol.exitRoom(sessionId, roomName, 0, 0);

                    SwingUtilities.invokeLater(() -> {
                        parentPanel.removeAll();
                        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
                        parentPanel.add(new WaitingRoomListPanel(parentPanel, in, out, sessionId));
                        parentPanel.add(new WaitingRoomCreationPanel(parentPanel, in, out, sessionId));
                        parentPanel.revalidate();
                        parentPanel.repaint();
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
    }
}

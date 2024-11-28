package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// 대기실 컴포넌트
public class WaitingRoomComponent extends JPanel {
    final private BufferedReader in;
    final private PrintWriter out;
    final private String sessionId;
    private JPanel parentPanel;
    private Set<String> sessionIds;

    public WaitingRoomComponent(BufferedReader in, PrintWriter out, String sessionId, String roomName, JPanel parentPanel, Set<String> sessionIds) {
        this.in = in;
        this.out = out;
        this.sessionId = sessionId;
        this.parentPanel = parentPanel;
        this.sessionIds = sessionIds;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(350, 70));

        JLabel nameLabel = new JLabel(roomName);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(nameLabel, BorderLayout.CENTER);

        // 입장 버튼을 오른쪽 아래에 작게 배치
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton enterButton = new JButton("입장");
        enterButton.setFocusPainted(false);
        enterButton.setPreferredSize(new Dimension(60, 30));
        buttonPanel.add(enterButton);

        add(buttonPanel, BorderLayout.SOUTH);

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = String.format("101|%s|%s|%d|%d|1", sessionId, roomName, 0, 0);
                out.println(message);
                out.flush();

                // 서버에 현재 대기실 세션 ID 요청
                new Thread(() -> {
                    try {
                        String response = in.readLine(); // 서버로부터 응답 읽기
                        System.out.println(response);
                        String[] tokens = response.split("\\|");
                        Set<String> currentSessionIds = new HashSet<>(Arrays.asList(tokens[1].split(",")));

                        SwingUtilities.invokeLater(() -> {
                            // GameRoomComponent로 전환
                            parentPanel.removeAll();
                            parentPanel.add(new InnerWaitingRoomComponent(parentPanel, currentSessionIds, roomName, in, out, sessionId));
                            parentPanel.revalidate();
                            parentPanel.repaint();
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace(); // 예외 로그
                    }
                }).start(); // 스레드 시작
            }
        });

    }
}

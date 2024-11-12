package org.kunp.WaitingRoomList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;

// 대기실 컴포넌트
public class WaitingRoomComponent extends JPanel {
    final private BufferedReader in;
    final private PrintWriter out;
    final private String sessionId;

    public WaitingRoomComponent(BufferedReader in, PrintWriter out, String sessionId, String roomName) {
        this.in = in;
        this.out = out;
        this.sessionId = sessionId;

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

        // 버튼 클릭 시 메시지 전송
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = String.format("0|%s|%d|%d|%d|1", sessionId, 0, 0, 0);
                out.println(message);
                out.flush();
            }
        });
    }
}

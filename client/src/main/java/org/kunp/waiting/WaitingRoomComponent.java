package org.kunp.waiting;

import org.kunp.inner.InnerWaitingRoomComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;

// 대기실 컴포넌트
public class WaitingRoomComponent extends JPanel {
    public WaitingRoomComponent(BufferedReader in, PrintWriter out, String sessionId, String roomName, JPanel parentPanel) {
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

                // GameRoomComponent로 전환
                parentPanel.removeAll();
                parentPanel.add(new InnerWaitingRoomComponent(parentPanel, roomName, in, out, sessionId));
                parentPanel.revalidate();
                parentPanel.repaint();
            }
        });
    }
}
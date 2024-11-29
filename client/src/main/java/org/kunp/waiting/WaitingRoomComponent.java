package org.kunp.waiting;

import org.kunp.inner.InnerWaitingRoomComponent;
import org.kunp.server.ServerProtocol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

// 대기실 컴포넌트
public class WaitingRoomComponent extends JPanel {
    final private BufferedReader in;
    final private PrintWriter out;
    final private String sessionId;
    private JPanel parentPanel;
    private Set<String> sessionIds;

    private ServerProtocol serverProtocol;
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

        // 입장 버튼 클릭 시 GameRoomComponent로 전환
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> currentSessionIds = null;
                try {
                    currentSessionIds = serverProtocol.enterRoom(sessionId, roomName, 0,0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                // GameRoomComponent로 전환
                parentPanel.removeAll();
                parentPanel.add(new InnerWaitingRoomComponent(currentSessionIds, roomName, in, out, sessionId));
                parentPanel.revalidate();
                parentPanel.repaint();
            }
        });
    }
}
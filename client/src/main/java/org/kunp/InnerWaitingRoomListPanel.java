package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InnerWaitingRoomListPanel extends JPanel {
    private JPanel gridPanel;

    public InnerWaitingRoomListPanel(List<String> sessionIds) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(350, 150));

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2열 그리드 레이아웃
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        updateSessionList(sessionIds);
    }

    public void updateSessionList(List<String> sessionIds) {
        gridPanel.removeAll(); // 기존 컴포넌트 제거

        // 짝수 인원만 추가
        if (sessionIds.size() % 2 != 0) {
            sessionIds.add("대기 중"); // 임시로 빈 자리 추가
        }

        for (String id : sessionIds) {
            JPanel userPanel = new JPanel();
            userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            userPanel.add(new JLabel(id));
            gridPanel.add(userPanel);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}


package org.kunp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

// 대기실 생성 패널
class WaitingRoomCreationPanel extends JPanel {
    private JTextField roomNameField;
    private JTextField timeLimitField;
    private JTextField playerLimitField;

    private final BufferedReader in;
    private final PrintWriter out;
    private final String sessionId;

    public WaitingRoomCreationPanel(BufferedReader in, PrintWriter out, String sessionId) {
        this.in = in;
        this.out = out;
        this.sessionId = sessionId;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(350, 150));

        // 대기실 생성 버튼
        JButton createRoomButton = new JButton("대기실 생성");
        createRoomButton.setFocusPainted(false);
        createRoomButton.setPreferredSize(new Dimension(100, 50));

        // GridBagLayout을 사용하여 입력 필드 패널 구성
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간격 설정

        // 대기실 생성을 위한 입력 필드 설정
        roomNameField = new JTextField(15);
        timeLimitField = new JTextField(15);
        playerLimitField = new JTextField(15);

        // 대기실 생성 버튼 위치 조정
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3; // 버튼을 입력 필드와 같은 높이로 설정
        gbc.insets = new Insets(0, 0, 0, 15); // 오른쪽 간격을 줘서 버튼과 입력 필드 간격 조정
        inputPanel.add(createRoomButton, gbc);

        gbc.gridheight = 1; // 다른 컴포넌트는 기본 높이로 설정
        gbc.insets = new Insets(5, 5, 5, 5); // 기본 간격으로 복원

        // 대기실 이름
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("대기실 이름"), gbc);
        gbc.gridx = 2;
        inputPanel.add(roomNameField, gbc);

        // 제한 시간
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("제한 시간"), gbc);
        gbc.gridx = 2;
        inputPanel.add(timeLimitField, gbc);

        // 제한 인원
        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("제한 인원"), gbc);
        gbc.gridx = 2;
        inputPanel.add(playerLimitField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // 대기실 생성 버튼 동작
        createRoomButton.addActionListener(e -> {
            String roomName = roomNameField.getText().trim();
            String timeLimitText = timeLimitField.getText().trim();
            String playerLimitText = playerLimitField.getText().trim();

            if (!roomName.isEmpty() && !timeLimitText.isEmpty() && !playerLimitText.isEmpty()) {
                try {
                    int timeLimit = Integer.parseInt(timeLimitText);
                    int playerLimit = Integer.parseInt(playerLimitText);

                    // 조건 확인: 제한 시간은 1 이상의 정수, 제한 인원은 2명 이상 8명 이하의 짝수
                    if (timeLimit <= 0) {
                        JOptionPane.showMessageDialog(this, "제한 시간은 1 이상의 분 단위 정수여야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    } else if (playerLimit < 2 || playerLimit > 8 || playerLimit % 2 != 0) {
                        JOptionPane.showMessageDialog(this, "제한 인원은 2명 이상 8명 이하의 짝수여야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // 대기실 생성
                        String message = String.format("0|%s|%d|%d|%d|1", sessionId, 0, 0, 0);
                        out.println(message);
                        out.flush();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "제한 시간과 제한 인원은 숫자여야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
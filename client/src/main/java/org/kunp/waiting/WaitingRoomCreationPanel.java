package org.kunp.waiting;

import org.kunp.manager.ScreenManager;
import org.kunp.manager.ServerCommunicator;
import org.kunp.manager.StateManager;
import org.kunp.inner.InnerWaitingRoomComponent;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class WaitingRoomCreationPanel extends JPanel {
    private final JTextField roomNameField;
    private final JTextField timeLimitField;
    private final JTextField playerLimitField;

    public WaitingRoomCreationPanel(StateManager stateManager, ScreenManager screenManager, ServerCommunicator serverCommunicator, BufferedReader in, PrintWriter out) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(350, 150));

        JButton createRoomButton = new JButton("대기실 생성");
        createRoomButton.setFocusPainted(false);
        createRoomButton.setPreferredSize(new Dimension(100, 50));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        roomNameField = new JTextField(15);
        timeLimitField = new JTextField(15);
        playerLimitField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.insets = new Insets(0, 0, 0, 15);
        inputPanel.add(createRoomButton, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("대기실 이름"), gbc);
        gbc.gridx = 2;
        inputPanel.add(roomNameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("제한 시간"), gbc);
        gbc.gridx = 2;
        inputPanel.add(timeLimitField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("제한 인원"), gbc);
        gbc.gridx = 2;
        inputPanel.add(playerLimitField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        createRoomButton.addActionListener(e -> {
            String roomName = roomNameField.getText().trim();
            String timeLimitText = timeLimitField.getText().trim();
            String playerLimitText = playerLimitField.getText().trim();

            if (!roomName.isEmpty() && !timeLimitText.isEmpty() && !playerLimitText.isEmpty()) {
                try {
                    int timeLimit = Integer.parseInt(timeLimitText);
                    int playerLimit = Integer.parseInt(playerLimitText);

                    if (timeLimit <= 0 || playerLimit < 2 || playerLimit > 8 || playerLimit % 2 != 0 ) {
                        JOptionPane.showMessageDialog(this, "입력 오류: 제한 시간과 인원을 확인하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    } else if (roomName.contains(",") || isPositiveInteger(roomName)) {
                        JOptionPane.showMessageDialog(this, "입력 오류: 방 이름을 확인하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        String message = String.format("102|%s|%s|%d|%d", stateManager.getSessionId(), roomName, playerLimit, timeLimit);
                        stateManager.sendServerRequest(message, () -> {});

                        message = String.format("101|%s|%s|%d|%d", stateManager.getSessionId(), roomName, playerLimit, timeLimit);
                        screenManager.addScreen("InnerWaitingRoom", new InnerWaitingRoomComponent(stateManager, serverCommunicator, screenManager, roomName, in ,out));
                        stateManager.sendServerRequest(message, () -> {
                            stateManager.switchTo("InnerWaitingRoom");
                        });
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "제한 시간과 제한 인원은 숫자여야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private boolean isPositiveInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+"); // 숫자로만 구성되어 있는지 확인
    }
}


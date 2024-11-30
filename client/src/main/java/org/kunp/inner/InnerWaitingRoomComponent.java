package org.kunp.inner;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

public class InnerWaitingRoomComponent extends JPanel {

  public InnerWaitingRoomComponent(
          JPanel parentPanel,
          String roomName,
          BufferedReader in,
          PrintWriter out,
          String sessionId) {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.GRAY));
    setPreferredSize(new Dimension(350, 200));

    JLabel nameLabel = new JLabel("게임 룸");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(nameLabel, BorderLayout.NORTH);

    Set<String> sessionIds = new HashSet<>();
    // 사용자 목록 패널 추가
    InnerWaitingRoomListPanel listPanel = new InnerWaitingRoomListPanel(sessionIds);
    add(listPanel, BorderLayout.CENTER);

    // 컨트롤 패널 추가
    InnerWaitingRoomControlPanel controlPanel =
            new InnerWaitingRoomControlPanel(parentPanel, roomName, in, out, sessionId);
    add(controlPanel, BorderLayout.SOUTH);

    // 서버 메시지 수신 및 처리 스레드
    Thread thread = new Thread(() -> {
      try {
        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received: " + message);
          String[] tokens = message.split("\\|");

          if (tokens.length > 1) {
            String type = tokens[0]; // 메시지 타입 (예: 110)
            String[] data = tokens[1].split(",");

            SwingUtilities.invokeLater(() -> {
              switch (type) {
                case "110": // 새 사용자 입장
                  sessionIds.addAll(List.of(data));
                  break;
                case "111": // 사용자 퇴장
                  sessionIds.remove(data[0]);
                  break;
                default:
                  System.out.println("Unhandled message type: " + type);
                  return;
              }
              listPanel.updateSessionList(sessionIds); // UI 갱신
            });
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }
}

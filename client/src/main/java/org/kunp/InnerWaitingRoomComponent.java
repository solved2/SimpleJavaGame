package org.kunp;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import javax.swing.*;

public class InnerWaitingRoomComponent extends JPanel {
  private final String sessionId;
  private final Set<String> sessionIds;
  private final BufferedReader in;
  private final PrintWriter out;

  public InnerWaitingRoomComponent(
      Set<String> sessionIds,
      String roomName,
      BufferedReader in,
      PrintWriter out,
      String sessionId) {
    this.sessionIds = sessionIds;
    this.in = in;
    this.out = out;
    this.sessionId = sessionId;

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.GRAY));
    setPreferredSize(new Dimension(350, 200));

    JLabel nameLabel = new JLabel("게임 룸");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(nameLabel, BorderLayout.NORTH);

    // 사용자 목록 패널 추가
    InnerWaitingRoomListPanel listPanel = new InnerWaitingRoomListPanel(sessionIds);
    add(listPanel, BorderLayout.CENTER);

    // 컨트롤 패널 추가
    IntterWaitingRoomControlPanel controlPanel =
        new IntterWaitingRoomControlPanel(roomName, out, sessionId);
    add(controlPanel, BorderLayout.SOUTH);



    Thread thread = new Thread(() -> {
      sessionIds.clear();
      try {
          String message;
        while ((message = in.readLine()) != null) {
          System.out.println(message);
          String[] tokens = message.split("\\|");
          sessionIds.add(tokens[1]);
          listPanel.updateSessionList(sessionIds);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }
}

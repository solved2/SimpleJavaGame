package org.kunp.inner;

import org.kunp.ScreenManager;
import org.kunp.ServerCommunicator;
import org.kunp.StateManager;
import org.kunp.map.Map;
import org.kunp.map.Player;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;


public class InnerWaitingRoomComponent extends JPanel {
  public InnerWaitingRoomComponent(StateManager stateManager, ServerCommunicator serverCommunicator, ScreenManager screenManager, String roomName, BufferedReader in, PrintWriter out) {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.GRAY));
    setPreferredSize(new Dimension(350, 200));

    JLabel nameLabel = new JLabel("게임 룸");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(nameLabel, BorderLayout.NORTH);

    Set<String> sessionIds = new HashSet<>();
    InnerWaitingRoomListPanel listPanel = new InnerWaitingRoomListPanel(sessionIds);
    add(listPanel, BorderLayout.CENTER);

    InnerWaitingRoomControlPanel controlPanel = new InnerWaitingRoomControlPanel(stateManager, screenManager, serverCommunicator, roomName, in, out);
    add(controlPanel, BorderLayout.SOUTH);

    // 메시지 리스너 등록
    ServerCommunicator.ServerMessageListener listener = message -> {
      handleServerMessage( message, sessionIds, listPanel);
    };

    stateManager.addMessageListener(listener);
  }

  private void handleServerMessage(String message, Set<String> sessionIds, InnerWaitingRoomListPanel listPanel) {
    String[] tokens = message.split("\\|");
    System.out.println(message);

    if (tokens.length > 1) {
      String type = tokens[0];
      if (type.equals("110")) {
        String[] sessions = message.split("\n");
        String lastSessionMessage = sessions[sessions.length - 1];
        String[] sessionData = lastSessionMessage.split("\\|");
        if (sessionData.length > 1) {
          String[] sessionIdsArray = sessionData[1].split(",");
          Set<String> newSessionIds = Set.of(sessionIdsArray);
          sessionIds.addAll(newSessionIds);
        }
      }
      else if (type.equals("111")) { // 사용자 퇴장
        String[] data = tokens[1].split(",");
        sessionIds.remove(data[0]);
      } else {
        System.out.println("Unhandled message type: " + type);
      }
      SwingUtilities.invokeLater(() -> listPanel.updateSessionList(sessionIds));
    }
  }
}
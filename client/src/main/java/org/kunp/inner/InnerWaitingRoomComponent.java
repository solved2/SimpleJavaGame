package org.kunp.inner;

import org.kunp.ServerCommunicator;
import org.kunp.StateManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class InnerWaitingRoomComponent extends JPanel {
  public InnerWaitingRoomComponent(StateManager stateManager, ServerCommunicator serverCommunicator, String roomName) {
    System.out.println("move inner");
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.GRAY));
    setPreferredSize(new Dimension(350, 200));

    JLabel nameLabel = new JLabel("게임 룸");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(nameLabel, BorderLayout.NORTH);

    Set<String> sessionIds = new HashSet<>();
    InnerWaitingRoomListPanel listPanel = new InnerWaitingRoomListPanel(sessionIds);
    add(listPanel, BorderLayout.CENTER);

    InnerWaitingRoomControlPanel controlPanel = new InnerWaitingRoomControlPanel(stateManager, roomName);
    add(controlPanel, BorderLayout.SOUTH);

    // 메시지 리스너 등록
    ServerCommunicator.ServerMessageListener listener = message -> {
      System.out.println("inner message");
      handleServerMessage(stateManager, message, sessionIds, listPanel);
    };

    stateManager.addMessageListener(listener);
  }

  private void handleServerMessage(StateManager stateManager, String message, Set<String> sessionIds, InnerWaitingRoomListPanel listPanel) {
    String[] tokens = message.split("\\|");

    if (tokens.length > 1) {
      String type = tokens[0];
      System.out.println(type);
      if (type.equals("110")) {
        String[] sessions = message.split("\n");
        String lastSessionMessage = sessions[sessions.length - 1];
        String[] sessionData = lastSessionMessage.split("\\|");
        if (sessionData.length > 1) {
          sessionIds.clear();
          String[] sessionIdsArray = sessionData[1].split(",");
          sessionIds.addAll(Set.of(sessionIdsArray));
        }
      }
      else if (type.equals("111")) { // 사용자 퇴장
        String[] data = tokens[1].split(",");
        sessionIds.remove(data[0]);
      } else if (type.equals("113")) { // 게임 시작
        stateManager.switchTo("Map");
      } else {
        System.out.println("Unhandled message type: " + type);
      }

      SwingUtilities.invokeLater(() -> listPanel.updateSessionList(sessionIds));
    }
  }

}



package org.kunp.inner;

import org.kunp.ServerCommunicator;
import org.kunp.StateManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class InnerWaitingRoomComponent extends JPanel {
  public InnerWaitingRoomComponent(StateManager stateManager, ServerCommunicator serverCommunicator, String roomName) {
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
      if (stateManager.getCurrentScreen().equals("InnerWaitingRoom")) {
        handleServerMessage(stateManager, message, sessionIds, listPanel);
      }
    };

    stateManager.addMessageListener(listener);

    // 컴포넌트가 비활성화될 때 리스너 제거
    addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentHidden(java.awt.event.ComponentEvent e) {
        stateManager.removeMessageListener(listener);
      }
    });
  }

  private void handleServerMessage(StateManager stateManager, String message, Set<String> sessionIds, InnerWaitingRoomListPanel listPanel) {
    String[] tokens = message.split("\\|");
    if (tokens.length > 1) {
      String type = tokens[0];
      String[] data = tokens[1].split(",");

      switch (type) {
        case "110": // 새 사용자 입장
          sessionIds.addAll(Set.of(data));
          break;
        case "111": // 사용자 퇴장
          sessionIds.remove(data[0]);
          break;
        case "113": // 게임 시작
          stateManager.switchTo("Map");
          break;
        default:
          System.out.println("Unhandled message type: " + type);
      }

      SwingUtilities.invokeLater(() -> listPanel.updateSessionList(sessionIds));
    }
  }
}



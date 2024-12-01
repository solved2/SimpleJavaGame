package org.kunp;

import javax.swing.*;

public class StateManager {
    private final ScreenManager screenManager;
    private final ServerCommunicator serverCommunicator;
    private final String sessionId;
    private String currentScreen; // 현재 화면을 추적하는 필드 추가

    public StateManager(ScreenManager screenManager, ServerCommunicator serverCommunicator, String sessionId) {
        this.screenManager = screenManager;
        this.serverCommunicator = serverCommunicator;
        this.sessionId = sessionId;
        this.currentScreen = ""; // 초기화
    }

    public String getSessionId() {
        return sessionId;
    }

    public void switchTo(String screenName) {
        currentScreen = screenName; // 현재 화면 업데이트
        SwingUtilities.invokeLater(() -> screenManager.showScreen(screenName));
    }

    public String getCurrentScreen() {
        return currentScreen; // 현재 화면 반환
    }

    public void sendServerRequest(String request, Runnable onSuccess) {
        serverCommunicator.sendRequest(request);
        SwingUtilities.invokeLater(onSuccess);
    }

    public void addMessageListener(ServerCommunicator.ServerMessageListener listener) {
        serverCommunicator.addMessageListener(listener);
    }
}



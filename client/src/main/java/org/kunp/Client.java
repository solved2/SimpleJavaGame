package org.kunp;

import org.kunp.result.ResultComponent;
import org.kunp.waiting.WaitingRoomCreationPanel;
import org.kunp.waiting.WaitingRoomListPanel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String sessionId;

    public Client() {
        try {
            // 서버와 연결
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 서버에서 세션 ID 수신
            sessionId = in.readLine();
            System.out.println("Connected with session ID: " + sessionId);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버 연결 실패: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 화면 관리 및 상태 초기화
        ScreenManager screenManager = new ScreenManager();
        ServerCommunicator serverCommunicator = new ServerCommunicator(in, out);
        StateManager stateManager = new StateManager(screenManager, serverCommunicator, sessionId);

        // 프레임 설정
        JFrame frame = new JFrame("Tag Game - 대기실");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setSize(500, 500); // 화면 크기
        frame.pack();
        frame.setLayout(new BorderLayout());
        Insets insets = frame.getInsets();
        int width = 500 + insets.left + insets.right;
        int height = 500 + insets.top + insets.bottom;
        frame.setSize(width, height);

        // 화면 등록
        registerScreens(sessionId, screenManager, stateManager, serverCommunicator);

        // 화면 전환
        screenManager.showScreen("WaitingRoom");

        frame.add(screenManager.getMainPanel(), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    /**
     * 화면 등록 메서드
     * @param screenManager 화면 관리 객체
     * @param stateManager 상태 관리 객체
     */
    private void registerScreens(String sessionId, ScreenManager screenManager, StateManager stateManager, ServerCommunicator serverCommunicator) {
        // 대기실 화면
        JPanel waitingRoomPanel = new JPanel(new BorderLayout());
        waitingRoomPanel.add(new WaitingRoomListPanel(sessionId, stateManager, serverCommunicator, screenManager, in, out), BorderLayout.CENTER);
        waitingRoomPanel.add(new WaitingRoomCreationPanel(stateManager, screenManager, serverCommunicator, in, out), BorderLayout.SOUTH);
        screenManager.addScreen("WaitingRoom", waitingRoomPanel);

        // Map 화면 (게임 화면)
        screenManager.addScreen("Map", new JPanel());
    }
}

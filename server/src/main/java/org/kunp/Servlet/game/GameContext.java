package org.kunp.Servlet.game;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.kunp.Servlet.session.Session;

public class GameContext {

  private static final int JAIL_X = 0; // 감옥 x 좌표
  private static final int JAIL_Y = 0; // 감옥 y 좌표
  private static final int JAIL_ROOM_NUMBER = 1; // 감옥 roomNumber

  private final Map<String, OutputStream> participants = new ConcurrentHashMap<>();
  private final Map<String, int[]> positions = new ConcurrentHashMap<>();
  private final Map<String, Integer> isChaser = new HashMap<>();
  private final Set<String> toRemove = new HashSet<>();
  private final Map<String, Boolean> playerStates = new ConcurrentHashMap<>(); // Captured은 true

  private final int gameId;
  private final AtomicBoolean isStarted = new AtomicBoolean(false);
  private final AtomicBoolean isFinished;
  private final int userlimit;
  private final int timelimit;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public GameContext(int gameId, AtomicBoolean isFinished, int userlimit, int timelimit) {
    this.gameId = gameId;
    this.isFinished = isFinished;
    this.userlimit = userlimit;
    this.timelimit = timelimit;
  }

  public void startTimer() {
    startTime = LocalDateTime.now();  // 게임 시작 시간 기록
    endTime = startTime.plusSeconds(timelimit);  // 게임 종료 시간 계산
    System.out.println("Game started.");
  }

  // 타임아웃 체크
  public boolean isTimeOut() {
    if (endTime != null) {
      // 현재 시간이 종료 시간을 넘었는지 확인
      return Duration.between(LocalDateTime.now(), endTime).isNegative();
    }
    return false;
  }

  public boolean isFinished() {
    return this.isFinished.get();
  }

  public void initializePlayerStates() {
    for (String sessionId : participants.keySet()) {
      // 모든 참가자 기본값 설정
      isChaser.putIfAbsent(sessionId, 1); // 1 = 도망자
      playerStates.putIfAbsent(sessionId, false); // false = 자유 상태
    }
  }

  public void updateContext(String sessionId, int x, int y, int roomId) {
    if (!isStarted.get() || startTime == null) {
      System.out.println("Game has not started or timer not initialized.");
      return;
    }

    // 도망자 상태 초기화 확인
    if (playerStates.isEmpty() || isChaser.isEmpty()) {
      System.out.println("Player states or chaser states are not initialized.");
      return;
    }

    determineWinner();
    // 감옥에 갇힌 상태라면 업데이트 차단
    if (playerStates.getOrDefault(sessionId, false)) {
      System.out.println("Player " + sessionId + " is in jail and cannot move.");
      return;
    }

    // 위치 정보 업데이트
    this.positions.putIfAbsent(sessionId, new int[4]);
    this.positions.get(sessionId)[0] = x;
    this.positions.get(sessionId)[1] = y;
    this.positions.get(sessionId)[2] = roomId;
    this.positions.get(sessionId)[3] = isChaser.getOrDefault(sessionId, 1);
  }


  public void updateAndBroadCast() {
    for (Map.Entry<String, OutputStream> participant : participants.entrySet()) {
      try {
        OutputStream oos = participant.getValue();
        for (Map.Entry<String, int[]> entry : positions.entrySet()) {
          oos.write(createMessage(210, entry.getValue(), entry.getKey(), this.gameId).getBytes());
          oos.flush();
        }
      } catch (IOException e) {
        toRemove.add(participant.getKey());
      }
    }
    removeDisconnectedParticipants();
  }

  public void enter(Session session) {
    if(participants.containsKey(session.getSessionId())) return;
    participants.put(session.getSessionId(), (OutputStream) session.getAttributes().get("ops"));
    positions.put(session.getSessionId(), new int[4]);
  }

  public void leave(Session session) {
    participants.remove(session.getSessionId());
  }

  public void setChasers() {
    initializePlayerStates();
    List<String> keys = new ArrayList<>(participants.keySet());
    if (keys.size() < 2) {
      throw new IllegalStateException("Not enough participants to select two chasers");
    }
    Random random = new Random();
    String chaser1 = keys.get(random.nextInt(keys.size()));
    String chaser2;
    do {
      chaser2 = keys.get(random.nextInt(keys.size()));
    } while (chaser1.equals(chaser2));
    setChaser(chaser1);
    setChaser(chaser2);
    for (Map.Entry<String, OutputStream> entry : participants.entrySet()) {
      try {
        entry.getValue().write(String.format("113|%d|%d|%d|%d\n", gameId, isChaser.getOrDefault(entry.getKey(), 1), positions.get(entry.getKey())[0], positions.get(entry.getKey())[1]).getBytes());
        entry.getValue().flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    this.isStarted.set(true);
    startTimer(); // 타이머 시작
  }

  private void setChaser(String sessionId) {
    isChaser.put(sessionId, 0);
  }

  public boolean isEmpty() {
    return participants.isEmpty();
  }

  private String createMessage(int type, int[] position, String id, int gameId) {
    return type + "|" + id + "|" + position[0] + "|" + position[1] + "|" + position[2] + "|" + gameId+ "|" + position[3] + "\n";
  }

  public void updateInteraction(String id, int roomNumber) throws IOException {
    int[] pos = positions.get(id);
    if (pos == null) return; // 유효하지 않은 사용자 무시

    if (isChaser.getOrDefault(id, 1) != 0) { // 도망자
      handleRunnerInteraction(id, roomNumber);
    } else { // 술래
      handleChaserInteraction(id, roomNumber);
    }
  }

  private void handleRunnerInteraction(String id, int roomNumber) throws IOException {
    int[] runnerPos = positions.get(id);
    if (runnerPos == null || playerStates.getOrDefault(id, false)) {
      // 감옥에 갇힌 경우 상호작용 불가.
      return;
    }

    // 감옥 주변에 있는지 확인
    for (Map.Entry<String, Boolean> entry : playerStates.entrySet()) {
      String targetId = entry.getKey();
      if (!entry.getValue()) continue; // 감옥에 갇힌 상태가 아닌 경우 무시

      int[] targetPos = positions.get(targetId);
      if (targetPos != null && isNearJail(runnerPos, targetPos)) {
        // 감옥에 갇힌 도망자 풀어줌
        playerStates.put(targetId, false); // 상태 업데이트 (갇힌 상태 해제)

        // 브로드캐스트: 도망자가 풀렸다
        String response = createFreedResponse(212, targetId, targetPos[0], targetPos[1], roomNumber, gameId);
        sendMessageToAll(response);
      }
    }
  }

  private void handleChaserInteraction(String id, int roomNumber) throws IOException {
    int[] chaserPos = positions.get(id);
    if (chaserPos == null) return;

    for (Map.Entry<String, int[]> entry : positions.entrySet()) {
      String targetId = entry.getKey();
      int[] targetPos = entry.getValue();

      if (targetId.equals(id)) continue; // 자신 제외
      if (isChaser.getOrDefault(targetId, 1) == 0) continue; // 다른 술래 제외
      if (playerStates.getOrDefault(targetId, false)) continue; // 이미 감옥에 갇힌 경우 무시

      if (isAvailable(chaserPos, targetPos)) {
        // 도망자를 감옥으로 이동
        positions.put(targetId, new int[]{JAIL_X, JAIL_Y, JAIL_ROOM_NUMBER}); // 감옥 위치
        playerStates.put(targetId, true); // 상태 업데이트 (갇힌 상태)

        // 브로드캐스트: 도망자가 잡혔다
        String response = createCaughtResponse(211, targetId, JAIL_X, JAIL_Y, JAIL_ROOM_NUMBER, gameId);
        sendMessageToAll(response);
      }
    }
  }

  private boolean isNearJail(int[] runnerPos, int[] jailPos) {
    // 감옥 근처에 있는지 확인 (JAIL_X, JAIL_Y 기준)
    return Math.abs(runnerPos[0] - JAIL_X) < 10 && Math.abs(runnerPos[1] - JAIL_Y) < 10;
  }

  public void determineWinner() {
    if (isTimeOut()) {
      // 타임아웃이 된 경우, 도망자 승리
      sendGameResult(false); // 기본적으로 도망자가 승리
      System.out.println("Game time out. Runners win.");
    } else {
      boolean allRunnersCaptured = true; // 변수 이름 변경: 더 명확한 의미 전달

      // 술래가 모든 도망자를 잡았는지 확인
      for (Map.Entry<String, Boolean> entry : playerStates.entrySet()) {
        String playerId = entry.getKey();

        // 플레이어가 도망자인지 확인하고 잡혔는지 검사
        boolean isRunner = isChaser.getOrDefault(playerId, 0) == 0; // 기본값 0 = 도망자
        boolean isCaptured = entry.getValue(); // true = 잡힘, false = 자유 상태

        if (isRunner && !isCaptured) {
          // 잡히지 않은 도망자가 있는 경우
          allRunnersCaptured = false;
          break; // 추가 검사 불필요, 즉시 탈출
        }
      }

      if (allRunnersCaptured) {
        // 모든 도망자가 잡혔으면 술래가 승리
        sendGameResult(true); // 술래 승리
        System.out.println("All runners captured. Chasers win.");
      } else {
        // 도망자가 남아있거나 게임 진행 중인 경우
        System.out.println("Game continues. Not all runners are captured yet.");
      }
    }

  }


  public void sendGameResult(boolean chaserWon) {
    int win = chaserWon ? 0 : 1; // 0: 술래 승리, 1: 도망 승리
    String resultMessage = createGameResultMessage(213, gameId, win);

    // 브로드캐스트 게임 결과
    sendMessageToAll(resultMessage);

    // 게임 종료 상태로 설정
    isFinished.set(true);

    System.out.println("Game ended. Result sent to all players: " + (chaserWon ? "Chasers win" : "Runners win"));
  }

  private String createGameResultMessage(int type, int gameId, int win) {
    return String.format("%d|%d|%d\n", type, gameId, win);
  }

  private String createCaughtResponse(int type, String sessionId, int x, int y, int roomNumber, int gameId) {
    return String.format("%d|%s|%d|%d|%d|%d\n", type, sessionId, x, y, roomNumber, gameId);
  }

  private String createFreedResponse(int type, String sessionId, int x, int y, int roomNumber, int gameId) {
    return String.format("%d|%s|%d|%d|%d|%d\n", type, sessionId, x, y, roomNumber, gameId);
  }

  private void sendMessageToAll(String message) {
    for (OutputStream outputStream : participants.values()) {
      try {
        outputStream.write(message.getBytes());
        outputStream.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isAvailable(int[] pos1, int[] pos2) {
    return pos1[0] - pos2[0] < 10 && pos1[1] - pos2[1] < 10;
  }

  private void removeDisconnectedParticipants() {
    for (String key : toRemove) {
      for (Map.Entry<String, OutputStream> entry : participants.entrySet()) {
        try {
          entry.getValue().write(String.format("214|%s\n", key).getBytes());
          entry.getValue().flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        participants.remove(key);
        positions.remove(key);
      }
    }
    toRemove.clear();
  }
}



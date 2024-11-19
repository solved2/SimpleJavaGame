package org.kunp.Servlet.game;

import org.kunp.Servlet.message.Message;
import org.kunp.Servlet.session.Session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameContextRegistry {

  private static GameContextRegistry registry;
  private int gameId = 1;
  private final Map<Integer, GameContext> gameContexts = new ConcurrentHashMap<>();
  private final ExecutorService gameThreadPool = Executors.newFixedThreadPool(5);

  // Singleton Method
  public static GameContextRegistry getInstance() {
    if(registry == null) {
      registry = new GameContextRegistry();
    }
    return registry;
  }

  // Game Context Method
  public void createGameContext(String roomName, String hostId) {
    int roomNumber = this.gameId++;
    GameContext gc = new GameContext(roomNumber, new AtomicBoolean(false));
    registerGameContext(roomNumber, gc);
    Runnable gameThread = new GameThread(gc);
    // TODO: Reap thread.
    this.gameThreadPool.submit(gameThread);
  }

  public boolean hasGame(int gameId) {
    return gameContexts.containsKey(gameId);
  }

  public void update(Message message) {
    GameContext gc = gameContexts.get(message.getGameId());
    gc.updateContext(message.getId(), message.getX(), message.getY(), message.getRoomNumber());
  }

  public void subscribe(Session session, int gameId) {
    GameContext gc = gameContexts.get(gameId);
    gc.enter(session);
  }

  public void unsubscribe(Session session, int roomNumber) {
    GameContext gc = gameContexts.get(roomNumber);
    gc.leave(session);
    if(gc.isEmpty()) {
      gameContexts.remove(roomNumber);
    }
  }

  private void registerGameContext(int roomNumber, GameContext gc) {
    gameContexts.put(roomNumber, gc);
  }

  public void interact(Message message) throws IOException {
    GameContext gc = gameContexts.get(message.getGameId());
    gc.updateInteraction(message.getId(), message.getRoomNumber());
  }
}

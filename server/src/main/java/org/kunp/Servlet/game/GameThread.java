package org.kunp.Servlet.game;

public class GameThread implements Runnable{

  private final GameContext gameContext;

  public GameThread(GameContext gc) {
    this.gameContext = gc;
  }

  @Override
  public void run() {
      while(!gameContext.isFinished()) {
          try {
            gameContext.updateAndBroadCast();
            Thread.sleep(10);
          } catch (InterruptedException e) {
              System.out.println(e);
          }
          catch (Exception e) {
              System.out.println(e);
          }
      }
  }
}

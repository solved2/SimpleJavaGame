package org.kunp.Servlet.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionInMemoryStorage implements SessionStorage {

  private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

  @Override
  public void save(String key, Object value) {
    sessionMap.put(key, (Session) value);
  }

  @Override
  public Object get(String key) {
    return sessionMap.get(key);
  }

  @Override
  public void remove(String key) {
    sessionMap.remove(key);
  }

  @Override
  public void clear() {
    sessionMap.clear();
  }
}

package org.kunp.Servlet.session;

public interface SessionStorage {
  void save(String key, Object value);

  Object get(String key);

  void remove(String key);

  void clear();

  int size();
}

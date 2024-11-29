package org.kunp.Servlet.session;

import static org.kunp.Servlet.session.Session.createEmptySession;

public class SessionManager {

  private static SessionManager sessionManager;
  private final SessionStorage sessionStorage;
  private final ISessionIdGenerator sessionIdGenerator;

  private SessionManager(SessionStorage sessionStorage, ISessionIdGenerator sessionIdGenerator) {
    this.sessionStorage = sessionStorage;
    this.sessionIdGenerator = sessionIdGenerator;
  }

  public static SessionManager getInstance() {
    if (sessionManager == null) {
      sessionManager = new SessionManager(new SessionInMemoryStorage(), new SessionIdGenerator());
    }
    return sessionManager;
  }

  public Session createSession() {
    Session session = createEmptySession();
    String sessionId = sessionIdGenerator.generateSessionId();
    session.setSessionId(sessionId);
    sessionStorage.save(sessionId, session);
    return session;
  }

  public Session getSession(String sessionId) {
    return (Session) sessionStorage.get(sessionId);
  }

  public int getSessionCount() {
    return sessionStorage.size();
  }
  public void removeSession(String sessionId) {
    sessionStorage.remove(sessionId);
  }

  public void clear() {
    sessionStorage.clear();
  }
}

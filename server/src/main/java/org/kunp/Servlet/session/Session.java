package org.kunp.Servlet.session;

import java.io.Serializable;
import java.util.Map;
import org.kunp.ValidateUtils;

public class Session implements Serializable {
  private static final long serialVersionUID = 1L;
  private final transient long creationTime;
  private final transient long lastAccessedTime;
  private String sessionId;
  private Map<String, Object> attributes;

  /***
   * Constructor for session
   * @param sessionId
   * @param attributes
   * @param creationTime
   * @param lastAccessedTime
   */
  private Session(
      String sessionId, Map<String, Object> attributes, long creationTime, long lastAccessedTime) {
    this.sessionId = sessionId;
    this.attributes = attributes;
    this.creationTime = creationTime;
    this.lastAccessedTime = lastAccessedTime;
  }

  /***
   * Factory method for Empty session
   * @return Session object
   */
  public static Session createEmptySession() {
    return new Session(null, null, 0, 0);
  }

  /***
   * getter for creation time
   * @return sessionId
   */
  public String getSessionId() {
    return sessionId;
  }

  /***
   *  setter for session id
   * @param sessionId not null
   */
  public void setSessionId(String sessionId) {
    ValidateUtils.notNull(sessionId, "SessionId cannot be null");
    this.sessionId = sessionId;
  }

  /***
   * getter for Attributes
   * @return Map<String, Object> attributes
   */
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  /***
   * setter for session attributes
   * @param attributes Map<String, Object> not null
   */
  public void setAttributes(Map<String, Object> attributes) {
    ValidateUtils.notNull(attributes, "Attributes cannot be null");
    this.attributes = attributes;
  }
}

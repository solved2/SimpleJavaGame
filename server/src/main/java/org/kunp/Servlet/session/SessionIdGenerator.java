package org.kunp.Servlet.session;

import java.security.SecureRandom;
import java.util.Base64;

public class SessionIdGenerator implements ISessionIdGenerator {

  private static final SecureRandom secureRandom = new SecureRandom();
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

  @Override
  public String generateSessionId() {
    byte[] randomBytes = new byte[16];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }
}

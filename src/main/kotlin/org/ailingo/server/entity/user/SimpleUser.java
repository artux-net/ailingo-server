package org.ailingo.server.entity.user;

import java.time.Instant;
import java.util.UUID;

public interface SimpleUser {

    UUID getId();

    String getLogin();

    String getPassword();

    Instant getLastLoginAt();

}

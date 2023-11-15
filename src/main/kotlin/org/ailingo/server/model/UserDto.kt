package org.ailingo.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private UUID id;
    private String login;
    private String avatar;
    private int xp;
    private Instant registration;
    private Instant lastLoginAt;

}
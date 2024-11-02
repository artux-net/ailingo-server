package net.artux.ailingo.server.service.user.reset;


import net.artux.ailingo.server.model.Status;

import java.util.Collection;

public interface ResetService {

    Status sendResetPasswordLetter(String email);

    Status changePassword(String token, String password);

    Collection<String> getTokens();

}

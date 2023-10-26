package org.ailingo.server.service;


import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.RegisterUserDto;

public interface EmailService {

  void askForPassword(UserEntity user, String token);

  void sendRegisterLetter(RegisterUserDto user);

  void sendConfirmLetter(RegisterUserDto user, String token);

}

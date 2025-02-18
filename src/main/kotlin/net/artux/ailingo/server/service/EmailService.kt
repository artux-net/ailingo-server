package net.artux.ailingo.server.service;


import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.RegisterUserDto;

public interface EmailService {

  void askForPassword(UserEntity user, String token);

  void sendRegisterLetter(RegisterUserDto user);

  void sendConfirmLetter(RegisterUserDto user, String token);

}

package net.artux.ailingo.server.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import net.artux.ailingo.server.service.user.reset.ResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Hidden
@RequestMapping(PasswordController.RESET_PASSWORD_URL)
public class PasswordController {
    public final static String RESET_PASSWORD_URL = "/reset/password";
    private final ResetService resetService;

    @GetMapping
    public String passwordPage(Model model, @RequestParam("t") String token) {
        model.addAttribute("token", token);
        return "public/user/resetPassword";
    }

    @PostMapping
    public String passwordPage(@RequestParam("token") String token, @RequestParam("password") String password) {
        resetService.changePassword(token, password);
        return "public/user/passwordSuccess";
    }

}
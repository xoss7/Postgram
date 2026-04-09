package sn.edu.ept.postgram.authservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.authservice.dto.RegistrationRequestDto;
import sn.edu.ept.postgram.authservice.service.UserService;

import java.util.Map;

@AllArgsConstructor
@Controller
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
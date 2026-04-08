package sn.edu.ept.postgram.authservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import sn.edu.ept.postgram.authservice.dto.RegistrationRequestDto;
import sn.edu.ept.postgram.authservice.service.UserService;

import java.net.URI;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/account")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequestDto request) {
        userService.save(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Account created successfully. Please log in."));
    }
}
package sn.edu.ept.postgram.authservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.edu.ept.postgram.authservice.dto.RegistrationRequestDto;
import sn.edu.ept.postgram.authservice.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequestDto userDto) {
        userService.save(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Account created successfully. Please log in.");
    }
}
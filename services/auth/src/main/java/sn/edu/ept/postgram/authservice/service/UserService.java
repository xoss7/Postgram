package sn.edu.ept.postgram.authservice.service;

import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.authservice.config.EventPublisher;
import sn.edu.ept.postgram.authservice.dto.RegistrationRequestDto;
import sn.edu.ept.postgram.authservice.entity.User;
import sn.edu.ept.postgram.authservice.repository.UserRepository;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.UserRegisteredEvent;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public String save(RegistrationRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.username())) {
            throw new EntityExistsException(
                    "User with username " + requestDto.username() + " already exists"
            );
        }

        if (userRepository.existsByEmail(requestDto.email())) {
            throw new EntityExistsException("Email already in use");
        }

        User user = User.builder()
                .username(requestDto.username())
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .roles(List.of("USER"))
                .build();
        User newUser = userRepository.save(user);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .firstName(requestDto.firstName())
                .lastName(requestDto.lastName())
                .bio(requestDto.bio())
                .build();

        eventPublisher.publish(
                KafkaTopics.USER_REGISTERED.getEvent(),
                newUser.getId().toString(),
                event
        );

        return user.getId().toString();
    }
}
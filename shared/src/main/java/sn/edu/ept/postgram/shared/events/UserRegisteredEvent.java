package sn.edu.ept.postgram.shared.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRegisteredEvent(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String bio){}

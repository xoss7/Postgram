package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String bio){}

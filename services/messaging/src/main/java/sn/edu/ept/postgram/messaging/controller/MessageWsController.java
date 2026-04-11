package sn.edu.ept.postgram.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import sn.edu.ept.postgram.messaging.dto.SendMessageRequest;
import sn.edu.ept.postgram.messaging.services.MessageService;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageWsController {

    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request,
                            @Header("simpUser")Principal principal) {

        Jwt jwt = (Jwt) ((JwtAuthenticationToken) principal).getPrincipal();
        assert jwt != null;
        String senderUsername = jwt.getSubject();
        UUID senderId = UUID.fromString(jwt.getClaimAsString("user_id"));

        messageService.sendMessage(senderId, senderUsername, request);
    }
}
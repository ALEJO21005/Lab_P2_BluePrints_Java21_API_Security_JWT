package co.edu.eci.blueprints.controllers;

import co.edu.eci.blueprints.dto.websocket.BlueprintWebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BlueprintWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public BlueprintWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/blueprint.join")
    @SendTo("/topic/blueprints")
    public BlueprintWebSocketMessage joinBlueprint(@Payload BlueprintWebSocketMessage message,
                                                  SimpMessageHeaderAccessor headerAccessor) {
        // El cliente se une a las actualizaciones de blueprints
        return BlueprintWebSocketMessage.blueprintCreated(
                message.author(),
                "System",
                null
        );
    }

    /**
     * Envía notificación a todos los usuarios suscritos a un blueprint específico
     */
    public void notifyBlueprintUpdate(String author, String blueprintName, BlueprintWebSocketMessage message) {
        messagingTemplate.convertAndSend("/topic/blueprints/" + author + "/" + blueprintName, message);
    }

    /**
     * Envía notificación global sobre blueprints
     */
    public void notifyGlobalBlueprintUpdate(BlueprintWebSocketMessage message) {
        messagingTemplate.convertAndSend("/topic/blueprints", message);
    }
}
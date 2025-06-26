// Spring Boot Backend Changes for Day 2

// These changes will allow the backend to detect when users connect and disconnect
// from the WebSocket, and then broadcast these events to all other clients.

// 1. Create a new package: chat/src/main/java/com/example/chat/listener
//    Inside this package, create the following file:

// File: chat/src/main/java/com/example/chat/listener/WebSocketEventListener.java
// This class listens for WebSocket connection and disconnection events.
package com.example.chat.listener;

import com.example.chat.model.Message; // Import your Message model
import org.slf4j.Logger; // For logging
import org.slf4j.LoggerFactory; // For logging
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.context.event.EventListener; // Annotation for event listeners
import org.springframework.messaging.simp.SimpMessageSendingOperations; // To send messages to WebSocket clients
import org.springframework.messaging.simp.stomp.StompHeaderAccessor; // To access STOMP headers
import org.springframework.stereotype.Component; // Marks this as a Spring component
import org.springframework.web.socket.messaging.SessionConnectedEvent; // Event for WebSocket session connected
import org.springframework.web.socket.messaging.SessionDisconnectEvent; // Event for WebSocket session disconnected

import java.util.Objects; // For null-safe operations
import java.time.LocalDateTime; // For timestamping messages

@Component // Marks this class as a Spring-managed component, so it can be scanned and its event listeners can function.
public class WebSocketEventListener {

    // Logger for this class, used for console output.
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired // Spring automatically injects an instance of SimpMessageSendingOperations.
               // This is used to send messages to specific users or broadcast to topics.
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * Listens for WebSocket Session Connected events.
     * When a new user connects to the WebSocket, this method is triggered.
     * @param event The SessionConnectedEvent, containing information about the new session.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Retrieve the username from the session attributes that was stored during chat.addUser (Day 1).
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            logger.info("User connected: " + username);

            // Create a message to broadcast that a user has joined.
            Message joinMessage = new Message();
            joinMessage.setSender(username);
            joinMessage.setContent("joined!");
            joinMessage.setTimestamp(LocalDateTime.now()); // Set server-side timestamp

            // Send the join message to the public topic.
            // All clients subscribed to /topic/public will receive this message.
            messagingTemplate.convertAndSend("/topic/public", joinMessage);
        }
    }

    /**
     * Listens for WebSocket Session Disconnected events.
     * When a user disconnects from the WebSocket, this method is triggered.
     * @param event The SessionDisconnectEvent, containing information about the disconnected session.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Retrieve the username from the session attributes.
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            logger.info("User disconnected: " + username);

            // Create a message to broadcast that a user has left.
            Message leaveMessage = new Message();
            leaveMessage.setSender(username);
            leaveMessage.setContent("left!");
            leaveMessage.setTimestamp(LocalDateTime.now()); // Set server-side timestamp

            // Send the leave message to the public topic.
            // This allows clients to update their online user lists.
            messagingTemplate.convertAndSend("/topic/public", leaveMessage);
        }
    }
}

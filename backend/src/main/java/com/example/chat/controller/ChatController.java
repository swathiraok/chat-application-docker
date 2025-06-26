package com.example.chat.controller;

import com.example.chat.model.Message; // Import the Message model
import com.example.chat.repository.MessageRepository; // Import the MessageRepository
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.messaging.handler.annotation.MessageMapping; // For WebSocket message mapping
import org.springframework.messaging.handler.annotation.Payload; // For extracting message payload
import org.springframework.messaging.handler.annotation.SendTo; // For broadcasting messages over WebSocket
import org.springframework.messaging.simp.SimpMessageHeaderAccessor; // For accessing STOMP headers (like session attributes)
import org.springframework.stereotype.Controller; // Marks this as a Spring MVC controller (needed for @MessageMapping)
import org.springframework.web.bind.annotation.CrossOrigin; // For CORS on REST endpoints
import org.springframework.web.bind.annotation.GetMapping; // For GET REST endpoint
import org.springframework.web.bind.annotation.RequestMapping; // For REST request mapping
import org.springframework.web.bind.annotation.RestController; // For combined REST and WebSocket controller

import java.time.LocalDateTime;
import java.util.List; // For returning lists of messages

@RestController // Indicates this class provides RESTful services (e.g., for /api/messages).
@Controller // Also required for Spring's WebSocket message handling (@MessageMapping).
@RequestMapping("/api/messages") // Base path for REST endpoints in this controller.
@CrossOrigin(origins = "http://localhost:4200") // Allows HTTP requests from the Angular frontend.
public class ChatController {

    @Autowired // Spring automatically injects an instance of MessageRepository.
    private MessageRepository messageRepository;

    /**
     * REST Endpoint: Retrieves all chat messages from the database.
     * This is typically called by the frontend when a user logs in to load chat history.
     * Accessible via GET request to http://localhost:8080/api/messages.
     * @return A List of all Message objects, ordered by timestamp.
     */
    @GetMapping
    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderByTimestampAsc(); // Uses the custom query method from MessageRepository.
    }

    /**
     * WebSocket Endpoint: Handles incoming chat messages sent by clients.
     * Messages sent to "/app/chat.sendMessage" will be routed to this method.
     * @param chatMessage The Message object received from the client.
     * @return The same Message object, which will then be broadcasted to subscribers of "/topic/public".
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public") // Broadcasts the return value to all clients subscribed to "/topic/public".
    public Message sendMessage(@Payload Message chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now()); // Sets the server-side timestamp to ensure accuracy.
        messageRepository.save(chatMessage); // Saves the received message to MongoDB.
        return chatMessage; // The message is returned and broadcasted.
    }

    /**
     * WebSocket Endpoint: Handles new users joining the chat.
     * Messages sent to "/app/chat.addUser" will be routed to this method.
     * @param chatMessage The Message object (expected to contain the sender's username, content like "joined!").
     * @param headerAccessor Provides access to STOMP message headers, used to store the username in the WebSocket session.
     * @return The Message object, which will be broadcasted to "/topic/public" to announce a user joined.
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public") // Broadcasts the return value to all clients subscribed to "/topic/public".
    public Message addUser(@Payload Message chatMessage,
                           SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setTimestamp(LocalDateTime.now()); // Set server-side timestamp.
        // For simplicity, "user joined" messages are not saved to the database, but you could choose to.
        return chatMessage; // The message is returned and broadcasted.
    }
}

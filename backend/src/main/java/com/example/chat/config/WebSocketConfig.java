// Create a new package: chat/src/main/java/com/example/chat/config
// Inside this package, create the following file:

// File: chat/src/main/java/com/example/chat/config/WebSocketConfig.java
// Configuration for WebSocket and STOMP messaging.
package com.example.chat.config;

import org.springframework.context.annotation.Configuration; // Marks this as a configuration class
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // For configuring the message broker
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // Enables WebSocket message handling
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // For registering STOMP endpoints
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // Interface to configure WebSocket message handling

@Configuration // Indicates that this class provides Spring configuration.
@EnableWebSocketMessageBroker // This annotation enables WebSocket message handling, backed by a message broker.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers STOMP (Simple Text Oriented Messaging Protocol) endpoints.
     * These are the URLs clients will use to connect to our WebSocket server.
     * @param registry The registry for STOMP endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Defines the "/ws" endpoint. Clients will connect to ws://localhost:8080/ws.
        // .setAllowedOriginPatterns("*") allows connections from any origin.
        // IMPORTANT: In a production environment, you should restrict this to your frontend's actual domain(s)
        //            for security reasons (e.g., .setAllowedOriginPatterns("http://your-frontend-domain.com")).
        // .withSockJS() enables SockJS fallback options for browsers that don't fully support WebSockets,
        // providing a more robust connection.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Configures the message broker, which handles routing messages to and from clients.
     * @param registry The registry for configuring message brokers.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/app" is the prefix for client-side application destinations.
        // Messages sent by clients to destinations starting with "/app" (e.g., "/app/chat.sendMessage")
        // will be routed to @MessageMapping methods in your controllers.
        registry.setApplicationDestinationPrefixes("/app");

        // "/topic" is the prefix for client-side subscriptions.
        // Clients subscribe to destinations starting with "/topic" (e.g., "/topic/public") to receive messages.
        // The simple broker handles broadcasting messages to all subscribed clients.
        registry.enableSimpleBroker("/topic");
    }
}

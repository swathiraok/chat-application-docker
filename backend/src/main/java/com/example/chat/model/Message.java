// Create a new package: chat/src/main/java/com/example/chat/model
// Inside this package, create the following two files:

// File: chat/src/main/java/com/example/chat/model/Message.java
// MongoDB document model for a chat message.
package com.example.chat.model;

import lombok.Data; // Import Lombok's Data annotation
import org.springframework.data.annotation.Id; // Import Spring Data's Id annotation
import org.springframework.data.mongodb.core.mapping.Document; // Import Document annotation for MongoDB mapping

import java.time.LocalDateTime; // For timestamping messages

@Data // Lombok annotation: automatically generates getters, setters, equals(), hashCode(), and toString() methods.
@Document(collection = "messages") // Maps this Java class to a MongoDB collection named "messages".
public class Message {
    @Id // Marks this field as the primary key (identifier) for the MongoDB document.
    private String id;
    private String sender; // Stores the username of the message sender.
    private String content; // Stores the actual text content of the message.
    private LocalDateTime timestamp; // Stores the exact time the message was sent.

    // Default constructor: Required by Spring Data MongoDB for object instantiation.
    public Message() {
        this.timestamp = LocalDateTime.now(); // Automatically sets the timestamp when a new Message object is created.
    }

    // Parameterized constructor: For creating Message objects with specific sender and content.
    public Message(String sender, String content) {
        this(); // Calls the default constructor to ensure timestamp is set.
        this.sender = sender;
        this.content = content;
    }
}
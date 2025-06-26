// Create a new package: chat/src/main/java/com/example/chat/repository
// Inside this package, create the following two files:

// File: chat/src/main/java/com/example/chat/repository/MessageRepository.java
// Spring Data MongoDB repository for Message documents.
package com.example.chat.repository;

import com.example.chat.model.Message; // Import the Message model
import org.springframework.data.mongodb.repository.MongoRepository; // Import MongoRepository
import org.springframework.stereotype.Repository; // Import Repository annotation

import java.util.List; // For returning lists of messages

@Repository // Marks this interface as a Spring Data repository, allowing Spring to find and manage it.
public interface MessageRepository extends MongoRepository<Message, String> {
    // MongoRepository provides standard CRUD (Create, Read, Update, Delete) operations
    // for Message documents with String as their ID type (based on the @Id field in Message.java).
    // Examples of methods it provides: save(), findById(), findAll(), delete().

    // Custom query method: Spring Data can automatically generate queries
    // based on method names. This method fetches all messages and orders them by timestamp in ascending order.
    List<Message> findAllByOrderByTimestampAsc();
}
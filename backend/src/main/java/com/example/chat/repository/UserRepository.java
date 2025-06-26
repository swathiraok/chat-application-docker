package com.example.chat.repository;

import com.example.chat.model.User; // Import the User model
import org.springframework.data.mongodb.repository.MongoRepository; // Import MongoRepository
import org.springframework.stereotype.Repository; // Import Repository annotation

import java.util.Optional; // For methods that might return null, Optional helps prevent NullPointerExceptions

@Repository // Marks this interface as a Spring Data repository.
public interface UserRepository extends MongoRepository<User, String> {
    // MongoRepository provides standard CRUD operations for User documents with String as their ID type.

    // Custom query method: Finds a User by their username.
    // Optional is used because a user with the given username might not exist.
    Optional<User> findByUsername(String username);
}